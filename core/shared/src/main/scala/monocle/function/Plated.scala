package monocle.function

import monocle.{Setter, Traversal}

import scala.annotation.implicitNotFound
import scalaz.std.stream._
import scalaz.std.anyVal._
import scalaz.syntax.monad._
import scalaz.{Applicative, Monad, State, Traverse}

/**
  * [[Plated]] is a type-class for types which can extract their immediate
  * self-similar children.
  *
  * @tparam A the parent and child type of a [[Plated]]
  */
@implicitNotFound("Could not find an instance of Plated[${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Plated[A] extends Serializable { self =>
  def plate: Traversal[A, A]
}

trait PlatedFunctions {

  /** [[Traversal]] of immediate self-similar children */
  def plate[A](implicit P: Plated[A]): Traversal[A, A] = P.plate

  /** get the immediate self-similar children of a target */
  @inline def children[A: Plated](a: A): List[A] = plate[A].getAll(a)

  /** get all transitive self-similar elements of a target, including itself */
  def universe[A: Plated](a: A): Stream[A] = {
    val fold = plate[A].asFold
    def go(b: A): Stream[A] = b #:: fold.foldMap[Stream[A]](go)(b)
    go(a)
  }

  /**
    * rewrite a target by applying a rule as often as possible until it reaches
    * a fixpoint (this is an infinite loop if there is no fixpoint)
    */
  def rewrite[A: Plated](f: A => Option[A])(a: A): A =
  rewriteOf(plate[A].asSetter)(f)(a)

  /**
    * rewrite a target by applying a rule within a [[Setter]], as often as
    * possible until it reaches a fixpoint (this is an infinite loop if there is
    * no fixpoint)
    */
  def rewriteOf[A](l: Setter[A, A])(f: A => Option[A])(a: A): A = {
    def go(b: A): A = {
      val c = transformOf(l)(go)(b)
      f(c).fold(c)(go)
    }
    go(a)
  }

  /** transform every element */
  def transform[A: Plated](f: A => A)(a: A): A =
  transformOf(plate[A].asSetter)(f)(a)

  /** transform every element by applying a [[Setter]] */
  def transformOf[A](l: Setter[A, A])(f: A => A)(a: A): A =
  l.modify(b => transformOf(l)(f)(f(b)))(a)

  /** transforming counting changes */
  def transformCounting[A: Plated](f: A => Option[A])(a: A): (Int, A) = {
    transformM[A, State[Int, ?]] { b =>
      f(b).map(c => State((i: Int) => (i + 1, c)))
        .getOrElse(State.state(b))
    }(a).runZero

  }

  /** transforming every element using monadic transformation */
  def transformM[A: Plated, M[_]: Monad](f: A => M[A])(a: A): M[A] = {
    val l = plate[A]
    def go(c: A): M[A] =
      l.modifyF[M](b => f(b).flatMap(go))(c)
    go(a)
  }

}

object Plated extends PlatedFunctions {
  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/

  implicit def listPlated[A]: Plated[List[A]] = new Plated[List[A]] {
    val plate: Traversal[List[A], List[A]] = new Traversal[List[A], List[A]] {
      def modifyF[F[_]: Applicative](f: List[A] => F[List[A]])(s: List[A]): F[List[A]] =
        s match {
          case x :: xs => Applicative[F].map(f(xs))(x :: _)
          case Nil => Applicative[F].point(Nil)
        }
    }
  }

  implicit def streamPlated[A]: Plated[Stream[A]] = new Plated[Stream[A]] {
    val plate: Traversal[Stream[A], Stream[A]] = new Traversal[Stream[A], Stream[A]] {
      def modifyF[F[_]: Applicative](f: Stream[A] => F[Stream[A]])(s: Stream[A]): F[Stream[A]] =
        s match {
          case x #:: xs => Applicative[F].map(f(xs))(x #:: _)
          case Stream() => Applicative[F].point(Stream.empty)
        }
    }
  }

  implicit val stringPlated: Plated[String] = new Plated[String] {
    val plate: Traversal[String, String] = new Traversal[String, String] {
      def modifyF[F[_]: Applicative](f: String => F[String])(s: String): F[String] =
        s.headOption match {
          case Some(h) => Applicative[F].map(f(s.tail))(h.toString ++ _)
          case None => Applicative[F].point("")
        }
    }
  }

  implicit def vectorPlated[A]: Plated[Vector[A]] = new Plated[Vector[A]] {
    val plate: Traversal[Vector[A], Vector[A]] = new Traversal[Vector[A], Vector[A]] {
      def modifyF[F[_]: Applicative](f: Vector[A] => F[Vector[A]])(s: Vector[A]): F[Vector[A]] =
        s match {
          case h +: t => Applicative[F].map(f(t))(h +: _)
          case _ => Applicative[F].point(Vector.empty)
        }
    }
  }

  /************************************************************************************************/
  /** Scalaz instances                                                                            */
  /************************************************************************************************/
  import scalaz.{Cofree, Free, IList, ICons, INil, Tree}

  implicit def cofreePlated[S[_]: Traverse, A]: Plated[Cofree[S, A]] = new Plated[Cofree[S, A]] {
    val plate: Traversal[Cofree[S, A], Cofree[S, A]] = new Traversal[Cofree[S, A], Cofree[S, A]] {
      def modifyF[F[_]: Applicative](f: Cofree[S, A] => F[Cofree[S, A]])(s: Cofree[S, A]): F[Cofree[S, A]] =
        Applicative[F].map(Traverse[S].traverse(s.t.run)(f))(Cofree(s.head, _))
    }
  }

  implicit def freePlated[S[_]: Traverse, A]: Plated[Free[S, A]] = new Plated[Free[S, A]] {
    val plate: Traversal[Free[S, A], Free[S, A]] = new Traversal[Free[S, A], Free[S, A]] {
      def modifyF[F[_]: Applicative](f: Free[S, A] => F[Free[S, A]])(s: Free[S, A]): F[Free[S, A]] =
        s.resume.fold(
          as => Applicative[F].map(Traverse[S].traverse(as)(f))(Free.roll),
          x => Applicative[F].point(Free.point(x))
        )
    }
  }

  implicit def ilistPlated[A]: Plated[IList[A]] = new Plated[IList[A]] {
    val plate: Traversal[IList[A], IList[A]] = new Traversal[IList[A], IList[A]] {
      def modifyF[F[_]: Applicative](f: IList[A] => F[IList[A]])(s: IList[A]): F[IList[A]] =
        s match {
          case ICons(x, xs) => Applicative[F].map(f(xs))(x :: _)
          case INil() => Applicative[F].point(INil())
        }
    }
  }

  implicit def treePlated[A]: Plated[Tree[A]] = new Plated[Tree[A]] {
    val plate: Traversal[Tree[A], Tree[A]] = new Traversal[Tree[A], Tree[A]] {
      def modifyF[F[_]: Applicative](f: Tree[A] => F[Tree[A]])(s: Tree[A]): F[Tree[A]] =
        Applicative[F].map(Traverse[Stream].traverse(s.subForest)(f))(Tree.Node(s.rootLabel, _))
    }
  }
}
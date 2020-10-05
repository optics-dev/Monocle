package monocle.function

import monocle.{Setter, Traversal}

import scala.annotation.implicitNotFound
import cats.{Applicative, Monad, Traverse}
import cats.data.State
import cats.instances.int._
import cats.instances.lazyList._
import cats.syntax.flatMap._

/**
  * [[Plated]] is a type-class for types which can extract their immediate
  * self-similar children.
  *
  * @tparam A the parent and child type of a [[Plated]]
  */
@implicitNotFound(
  "Could not find an instance of Plated[${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
abstract class Plated[A] extends Serializable { self =>
  def plate: Traversal[A, A]
}

trait CommonPlatedFunctions {

  /** [[Traversal]] of immediate self-similar children */
  def plate[A](implicit P: Plated[A]): Traversal[A, A] = P.plate
}

trait PlatedFunctions extends CommonPlatedFunctions {

  /** get the immediate self-similar children of a target */
  @inline def children[A: Plated](a: A): List[A] = plate[A].getAll(a)

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
  def transformCounting[A: Plated](f: A => Option[A])(a: A): (Int, A) =
    transformM[A, State[Int, *]] { b =>
      f(b)
        .map(c => State((i: Int) => (i + 1, c)))
        .getOrElse(State.pure(b))
    }(a).runEmpty.value

  /** transforming every element using monadic transformation */
  def transformM[A: Plated, M[_]: Monad](f: A => M[A])(a: A): M[A] = {
    val l = plate[A]
    def go(c: A): M[A] =
      l.modifyF[M](b => f(b).flatMap(go))(c)
    go(a)
  }

  /** get all transitive self-similar elements of a target, including itself */
  def universe[A: Plated](a: A): LazyList[A] = {
    val fold                  = plate[A].asFold
    def go(b: A): LazyList[A] = b #:: fold.foldMap[LazyList[A]](go)(b)
    go(a)
  }
}

object Plated extends PlatedFunctions {
  def apply[A](traversal: Traversal[A, A]): Plated[A] =
    new Plated[A] {
      override val plate: Traversal[A, A] = traversal
    }

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def listPlated[A]: Plated[List[A]] =
    Plated(
      new Traversal[List[A], List[A]] {
        def modifyF[F[_]: Applicative](f: List[A] => F[List[A]])(s: List[A]): F[List[A]] =
          s match {
            case x :: xs => Applicative[F].map(f(xs))(x :: _)
            case Nil     => Applicative[F].pure(Nil)
          }
      }
    )

  implicit def lazyListPlated[A]: Plated[LazyList[A]] =
    Plated(
      new Traversal[LazyList[A], LazyList[A]] {
        def modifyF[F[_]: Applicative](f: LazyList[A] => F[LazyList[A]])(s: LazyList[A]): F[LazyList[A]] =
          s match {
            case x #:: xs   => Applicative[F].map(f(xs))(x #:: _)
            case LazyList() => Applicative[F].pure(LazyList.empty)
          }
      }
    )

  implicit val stringPlated: Plated[String] = Plated(
    new Traversal[String, String] {
      def modifyF[F[_]: Applicative](f: String => F[String])(s: String): F[String] =
        s.headOption match {
          case Some(h) => Applicative[F].map(f(s.tail))(h.toString ++ _)
          case None    => Applicative[F].pure("")
        }
    }
  )

  implicit def vectorPlated[A]: Plated[Vector[A]] =
    Plated(
      new Traversal[Vector[A], Vector[A]] {
        def modifyF[F[_]: Applicative](f: Vector[A] => F[Vector[A]])(s: Vector[A]): F[Vector[A]] =
          s match {
            case h +: t => Applicative[F].map(f(t))(h +: _)
            case _      => Applicative[F].pure(Vector.empty)
          }
      }
    )

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.Now
  import cats.data.Chain
  import cats.free.{Cofree, Free}

  implicit def chainPlated[A]: Plated[Chain[A]] =
    new Plated[Chain[A]] {
      val plate: Traversal[Chain[A], Chain[A]] =
        new Traversal[Chain[A], Chain[A]] {
          def modifyF[F[_]: Applicative](f: Chain[A] => F[Chain[A]])(s: Chain[A]): F[Chain[A]] =
            s.uncons match {
              case Some((x, xs)) => Applicative[F].map(f(xs))(_.prepend(x))
              case None          => Applicative[F].pure(Chain.empty)
            }
        }
    }

  implicit def cofreePlated[S[_]: Traverse, A]: Plated[Cofree[S, A]] =
    Plated(
      new Traversal[Cofree[S, A], Cofree[S, A]] {
        def modifyF[F[_]: Applicative](f: Cofree[S, A] => F[Cofree[S, A]])(s: Cofree[S, A]): F[Cofree[S, A]] =
          Applicative[F].map(Traverse[S].traverse(s.tail.value)(f))(t => Cofree(s.head, Now(t)))
      }
    )

  implicit def freePlated[S[_]: Traverse, A]: Plated[Free[S, A]] =
    Plated(
      new Traversal[Free[S, A], Free[S, A]] {
        def modifyF[F[_]: Applicative](f: Free[S, A] => F[Free[S, A]])(s: Free[S, A]): F[Free[S, A]] =
          s.resume.fold(
            as => Applicative[F].map(Traverse[S].traverse(as)(f))(Free.roll),
            x => Applicative[F].pure(Free.pure(x))
          )
      }
    )
}

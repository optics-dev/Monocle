package monocle

import scalaz._
import scalaz.std.stream._

/**
  * [[Plated]] is a type-class for types which can extract their immediate
  * self-similar children.
  *
  * @tparam A the parent and child type of a [[Plated]]
  */
abstract class Plated[A] extends Serializable { self =>
  def plate: Traversal[A, A]
}

object Plated {
  def plate[A: Plated]: Traversal[A, A] = implicitly[Plated[A]].plate
  def children[A: Plated](a: A): List[A] = plate[A].getAll(a)
  def universe[A: Plated](a: A): Stream[A] = {
    val fold = plate[A].asFold
    def go(b: A): Stream[A] = fold.foldMap[Stream[A]](c => c #:: go(c))(b)
    go(a)
  }
  def rewrite[A: Plated](f: A => Option[A])(a: A): A = {
    val setter = plate[A].asSetter
    def gogo(b: A): A = {
      def go(c: A): A = gogo(f(c).fold(c)(go))
      setter.modify(go)(b)
    }
    gogo(a)
  }
  def transform[A: Plated](f: A => A)(a: A): A = plate[A].modify(f)(a)

  implicit def freePlated[S[_]: Traverse, A]: Plated[Free[S, A]] = new Plated[Free[S, A]] {
    def plate: Traversal[Free[S, A], Free[S, A]] = new Traversal[Free[S, A], Free[S, A]] {
      def modifyF[F[_]: Applicative](f: Free[S, A] => F[Free[S, A]])(s: Free[S, A]): F[Free[S, A]] =
        s.resume.fold(
          as => Applicative[F].map(Traverse[S].traverse(as)(f)) {
            // Free.roll does not exist in 7.1
            Free.liftF(_).flatMap(identity)
          },
          x => Applicative[F].point(Free.point(x))
        )
    }
  }

  implicit def listPlated[A]: Plated[List[A]] = new Plated[List[A]] {
    def plate: Traversal[List[A], List[A]] = new Traversal[List[A], List[A]] {
      def modifyF[F[_]: Applicative](f: List[A] => F[List[A]])(s: List[A]): F[List[A]] =
        s match {
          case x :: xs => Applicative[F].map(f(xs))(x :: _)
          case Nil => Applicative[F].point(Nil)
        }
    }
  }

  implicit def ilistPlated[A]: Plated[IList[A]] = new Plated[IList[A]] {
    def plate: Traversal[IList[A], IList[A]] = new Traversal[IList[A], IList[A]] {
      def modifyF[F[_]: Applicative](f: IList[A] => F[IList[A]])(s: IList[A]): F[IList[A]] =
        s match {
          case ICons(x, xs) => Applicative[F].map(f(xs))(x :: _)
          case INil() => Applicative[F].point(INil())
        }
    }
  }

  implicit def treePlated[A]: Plated[Tree[A]] = new Plated[Tree[A]] {
    def plate: Traversal[Tree[A], Tree[A]] = new Traversal[Tree[A], Tree[A]] {
      def modifyF[F[_]: Applicative](f: Tree[A] => F[Tree[A]])(s: Tree[A]): F[Tree[A]] =
        Applicative[F].map(Traverse[Stream].traverse(s.subForest)(f))(Tree.node(s.rootLabel, _))
    }
  }

  implicit def cofreePlated[S[_]: Traverse, A]: Plated[Cofree[S, A]] = new Plated[Cofree[S, A]] {
    def plate: Traversal[Cofree[S, A], Cofree[S, A]] = new Traversal[Cofree[S, A], Cofree[S, A]] {
      def modifyF[F[_]: Applicative](f: Cofree[S, A] => F[Cofree[S, A]])(s: Cofree[S, A]): F[Cofree[S, A]] =
        Applicative[F].map(Traverse[S].traverse(s.t.run)(f))(Cofree(s.head, _))
    }
  }
}

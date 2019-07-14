package monocle.function

import cats.Applicative
import cats.instances.lazyList._
import monocle.Traversal

trait PlatedFunctionsScalaVersionSpecific extends CommonPlatedFunctions {
  /************************************************************************************************/
  /** 2.13 std functions                                                                          */
  /************************************************************************************************/
  /** get all transitive self-similar elements of a target, including itself */
  def universe[A: Plated](a: A): LazyList[A] = {
    val fold = plate[A].asFold
    def go(b: A): LazyList[A] = b #:: fold.foldMap[LazyList[A]](go)(b)
    go(a)
  }
}

trait PlatedInstancesScalaVersionSpecific {
  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListPlated[A]: Plated[LazyList[A]] = Plated(
    new Traversal[LazyList[A], LazyList[A]] {
      def modifyF[F[_]: Applicative](f: LazyList[A] => F[LazyList[A]])(s: LazyList[A]): F[LazyList[A]] =
        s match {
          case x #:: xs => Applicative[F].map(f(xs))(x #:: _)
          case LazyList() => Applicative[F].pure(LazyList.empty)
        }
    }
  )
}

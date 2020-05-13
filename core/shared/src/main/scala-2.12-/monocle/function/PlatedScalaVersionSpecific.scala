package monocle.function

import cats.Applicative
import cats.instances.stream._
import monocle.Traversal
import scala.collection.immutable.Stream.#::

trait PlatedFunctionsScalaVersionSpecific extends CommonPlatedFunctions {

  /************************************************************************************************/
  /** 2.12 std functions                                                                          */
  /************************************************************************************************/
  /** get all transitive self-similar elements of a target, including itself */
  def universe[A: Plated](a: A): Stream[A] = {
    val fold                = plate[A].asFold
    def go(b: A): Stream[A] = b #:: fold.foldMap[Stream[A]](go)(b)
    go(a)
  }

}

trait PlatedInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamPlated[A]: Plated[Stream[A]] =
    Plated(
      new Traversal[Stream[A], Stream[A]] {
        def modifyF[F[_]: Applicative](f: Stream[A] => F[Stream[A]])(s: Stream[A]): F[Stream[A]] =
          s match {
            case x #:: xs => Applicative[F].map(f(xs))(x #:: _)
            case Stream() => Applicative[F].pure(Stream.empty)
          }
      }
    )

}

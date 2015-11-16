package monocle.std

import monocle.function._
import monocle.Traversal

import scalaz.{Applicative, Free, Traverse}

object free extends FreeOptics

trait FreeOptics {

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

}

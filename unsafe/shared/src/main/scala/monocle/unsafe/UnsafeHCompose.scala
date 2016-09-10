package monocle.unsafe

import monocle.{PTraversal, Lens, Traversal}
import scalaz.Applicative

/**
  * TODO: needs scala doc
  */
object UnsafeHCompose {
  def unsafeHCompose[S, A](xs: Lens[S, A]*): Traversal[S, A] = {
    val lenses = xs.toList
    new PTraversal[S, S, A, A] {
      def modifyF[F[_]: Applicative](f: A => F[A])(s: S): F[S] = {
        val as: List[(F[A], Lens[S, A])] = lenses.map(lens => (f(lens.get(s)), lens))
        as.foldLeft(Applicative[F].pure(s)){ case (fs, (fa, lens)) =>
          Applicative[F].apply2(fa, fs)((a, s) => lens.set(a)(s))
        }
      }
    }
  }
}

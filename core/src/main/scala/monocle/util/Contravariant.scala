package monocle.util


trait Contravariant[F[_]] {

  def contramap[A, B](f: A => B)(fb: F[B]): F[A]

}

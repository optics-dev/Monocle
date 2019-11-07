package monocle

trait PTraversal[S, T, A, B] extends PSetter[S, T, A, B] { self =>
  def traverse[Z](z: Z, f: (Z, A) => Either[Z, (Z, B)]): S => (Z, T)

  final def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z = 
    traverse[Z](zero, (z, a) => Left(combine(z, f(a))))(s)._1

  final def set(b: B): S => T = modify(_ => b)

  final def modify(f: A => B): S => T = 
    (s: S) => traverse[Unit]((), (_, a) => Right(((), f(a))))(s)._2

  final def compose[C, D](that: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    new PTraversal[S, T, C, D] {
      def traverse[Z](z: Z, f: (Z, C) => Either[Z, (Z, D)]): S => (Z, T) = 
        self.traverse[Z](z, (z, a) => 
          that.traverse[Either[Z, Z]](Right(z), (ez, c) => ez match {
            case Left(z) => Left(Left(z))
            case Right(z) => f(z, c) match {
              case Left(z) => Left(Left(z))
              case Right((z, d)) => Right((Right(z), d))
            }
          })(a) match {
            case (Left(z), _) => Left(z)
            case (Right(z), b) => Right((z, b))
          })
    }

  final def choice[S1, T1](that: PTraversal[S1, T1, A, B]): PTraversal[Either[S, S1], Either[T, T1], A, B] =
    new PTraversal[Either[S, S1], Either[T, T1], A, B] {
      def traverse[Z](z: Z, f: (Z, A) => Either[Z, (Z, B)]): Either[S, S1] => (Z, Either[T, T1]) = 
        {
          case Left(s) => self.traverse(z, f)(s) match { case (z, t) => (z, Left(t)) }
          case Right(s1) => that.traverse(z, f)(s1) match { case (z, t1) => (z, Right(t1)) }
        }
    }
}
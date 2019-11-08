package monocle

sealed trait Fold[-S, +A] extends Serializable {
  def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z
}

sealed trait PFold[+LowerS, -UpperS, +LowerA, -UpperA] extends Serializable {
  def fold[Z, S >: LowerS <: UpperS, A >: LowerA <: UpperA](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z
}

object Fold {
  def apply[S, A](fn: FoldFunction[S, A]): Fold[S, A] = 
    new Fold[S, A] {
      def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z =
        fn(zero, combine)(f)(s)
    }
}

sealed trait Getter[-S, +A] extends Fold[S, A] { self =>
  def get(s: S): A
}

object Getter {

}

sealed trait PIso[-S, +T, +A, -B] extends PLens[S, T, A, B] with PPrism[S, T, A, B] { self =>

  // def reverse: PIso[B, A, T, S] = ???

  final def compose[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    new PIso[S, T, C, D] {
      def get(from: S): C =
        other.get(self.get(from))
      def reverseGet(to: D): T =
        self.reverseGet(other.reverseGet(to))
    }
}

object PIso {
  def apply[S, T, A, B](get0: S => A)(reverseGet0: B => T): PIso[S, T, A, B] =
    new PIso[S, T, A, B] {
      def get(from: S): A      = get0(from)
      def reverseGet(to: B): T = reverseGet0(to)
    }
}

sealed trait PLens[-S, +T, +A, -B] extends POptional[S, T, A, B] with Getter[S, A] { self =>

  final override def getOrModify(from: S): Either[Nothing, A] =
    Right(get(from))

  final def compose[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    new PLens[S, T, C, D] {
      def get(from: S): C    = other.get(self.get(from))
      def set(to: D): S => T = self.modify(other.set(to))
    }
}

object PLens {
  def apply[S, T, A, B](get0: S => A)(set0: (S, B) => T): PLens[S, T, A, B] =
    new PLens[S, T, A, B] {
      def get(s: S): A       = get0(s)
      def set(to: B): S => T = set0(_, to)
    }
}

sealed trait POptional[-S, +T, +A, -B] extends PSetter[S, T, A, B] { self =>

  def getOrModify(from: S): Either[T, A]

  def set(to: B): S => T

  final override def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z =
    getOrModify(s).fold(_ => zero, a => f(a))

  final def getOption(from: S): Option[A] =
    getOrModify(from).toOption

  final def modify(f: A => B): S => T =
    s => getOrModify(s).fold(t => t, a => set(f(a))(s))

  final def compose[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    new POptional[S, T, C, D] {
      def getOrModify(from: S): Either[T, C] =
        self.getOrModify(from).flatMap(a => other.getOrModify(a).left.map(self.set(_)(from)))
      def set(to: D): S => T =
        self.modify(other.set(to))
    }
}

object POptional {
  def apply[S, T, A, B](getOrModify0: S => Either[T, A])(set0: (S, B) => T): POptional[S, T, A, B] =
    new POptional[S, T, A, B] {
      def getOrModify(from: S): Either[T, A] = getOrModify0(from)
      def set(to: B): S => T                 = set0(_, to)
    }
}

sealed trait PPrism[-S, +T, +A, -B] extends POptional[S, T, A, B] { self =>

  def reverseGet(to: B): T

  final def set(to: B): S => T =
    _ => reverseGet(to)

  final def compose[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    new PPrism[S, T, C, D] {
      def getOrModify(from: S): Either[T, C] =
        self.getOrModify(from).flatMap(a => other.getOrModify(a).left.map(self.set(_)(from)))
      def reverseGet(to: D): T =
        self.reverseGet(other.reverseGet(to))
    }
}

object PPrism {
  def apply[S, T, A, B](getOption0: S => Either[T, A])(reverseGet: B => T): PPrism[S, T, A, B] =
    new PPrism[S, T, A, B] {
      def getOrModify(from: S): Either[T, A] = getOption0(from)
      def reverseGet(to: B): T               = reverseGet(to)
    }
}

sealed trait PSetter[-S, +T, +A, -B] extends Fold[S, A] { self =>

  def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z =
    zero

  /** modify polymorphically the target of a [[PSetter]] with a function */
  def modify(f: A => B): S => T

  /** set polymorphically the target of a [[PSetter]] with a value */
  def set(b: B): S => T
}

object PSetter {

  def apply[S, T, A, B](modify0: (S, A => B) => T)(set0: (S, B) => T): PSetter[S, T, A, B] =
    new PSetter[S, T, A, B] {
      def modify(f: A => B): S => T = modify0(_, f)
      def set(b: B): S => T = set0(_, b)

    }

}

sealed trait PTraversal[S, T, A, B] extends PSetter[S, T, A, B] { self =>
  def traverse[Z](z: Z, f: (Z, A) => Either[Z, (Z, B)]): S => (Z, T)

  final override def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z =
    traverse[Z](zero, (z, a) => Left(combine(z, f(a))))(s)._1

  final def set(b: B): S => T = modify(_ => b)

  final def modify(f: A => B): S => T =
    (s: S) => traverse[Unit]((), (_, a) => Right(((), f(a))))(s)._2

  final def compose[C, D](that: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    new PTraversal[S, T, C, D] {
      def traverse[Z](z: Z, f: (Z, C) => Either[Z, (Z, D)]): S => (Z, T) =
        s =>
          self.traverse[Either[Z, Z]](
            Right(z),
            (ez, a) =>
              that.traverse[Either[Z, Z]](
                ez,
                (ez, c) =>
                  ez.map(f(_, c)) match {
                    case Left(z) => Left(Left(z))
                    case Right(a) => a match {
                      case Left(z) => Left(Left(z))
                      case Right((z, d)) => Right((Right(z), d))
                    }
                  }
              )(a) match {
                case (Left(z), b) => ???
                case (Right(z), b) => ???
              }
          )(s) match { case (ez, t) => (ez.merge, t) }
    }

  final def choice[S1, T1](that: PTraversal[S1, T1, A, B]): PTraversal[Either[S, S1], Either[T, T1], A, B] =
    new PTraversal[Either[S, S1], Either[T, T1], A, B] {
      def traverse[Z](z: Z, f: (Z, A) => Either[Z, (Z, B)]): Either[S, S1] => (Z, Either[T, T1]) = {
        case Left(s)   => self.traverse(z, f)(s) match { case (z, t)   => (z, Left(t)) }
        case Right(s1) => that.traverse(z, f)(s1) match { case (z, t1) => (z, Right(t1)) }
      }
    }
}

object PTraversal {
  def apply[S, T, A, B](fn: TraverseFunction[S, T, A, B]): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def traverse[Z](z: Z, f: (Z, A) => Either[Z, (Z, B)]): S => (Z, T) = 
        fn(z, f)
    }
}

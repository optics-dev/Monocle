package monocle

import monocle.function.{At, Field1, Field2, Field3, Field4, Field5, Field6}

object Lens {
  def apply[S, A](_get: S => A)(_set: (S, A) => S): Lens[S, A] =
    new Lens[S, A] {
      def get(from: S): A    = _get(from)
      def set(to: A): S => S = _set(_, to)
    }

  def at[S, I, A](index: I)(implicit ev: At.Aux[S, I, A]): Lens[S, Option[A]] =
    ev.at(index)

  def first[S, A](implicit ev: Field1.Aux[S, A]): Lens[S, A] =
    ev.first

  def second[S, A](implicit ev: Field2.Aux[S, A]): Lens[S, A] =
    ev.second

  def third[S, A](implicit ev: Field3.Aux[S, A]): Lens[S, A] =
    ev.third

  def fourth[S, A](implicit ev: Field4.Aux[S, A]): Lens[S, A] =
    ev.fourth

  def fifth[S, A](implicit ev: Field5.Aux[S, A]): Lens[S, A] =
    ev.fifth

  def sixth[S, A](implicit ev: Field6.Aux[S, A]): Lens[S, A] =
    ev.sixth
}

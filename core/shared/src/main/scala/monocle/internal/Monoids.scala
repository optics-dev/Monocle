package monocle.internal

import cats.Monoid

private[monocle] object Monoids {
  def firstOption[A]: Monoid[Option[A]] =
    new Monoid[Option[A]] {
      def empty: Option[A]                               = None
      def combine(x: Option[A], y: Option[A]): Option[A] = x.orElse(y)
    }

  def lastOption[A]: Monoid[Option[A]] =
    new Monoid[Option[A]] {
      def empty: Option[A]                               = None
      def combine(x: Option[A], y: Option[A]): Option[A] = y.orElse(x)
    }

  val any: Monoid[Boolean] = new Monoid[Boolean] {
    def empty: Boolean                           = false
    def combine(x: Boolean, y: Boolean): Boolean = x || y
  }

  val all: Monoid[Boolean] = new Monoid[Boolean] {
    def empty: Boolean                           = true
    def combine(x: Boolean, y: Boolean): Boolean = x && y
  }
}

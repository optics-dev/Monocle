package monocle

import monocle.util.{Constant, Contravariant}
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._
import scalaz.{Applicative, Monoid}

trait Fold[S, A] { self =>

  protected def underlyingFold[F[_] : Contravariant : Applicative](from: S)(f: A => F[A]): F[S]

  def fold[B: Monoid](from: S)(f: A => B): B =
    underlyingFold[({type l[a] = Constant[B,a]})#l](from){ a: A => Constant[B, A](f(a))}.value

  def toListOf(from: S): List[A] = fold(from)(List(_))

  def headOption(from: S): Option[A] = fold(from)(Option(_).first)

  def exist(from: S)(p: A => Boolean): Boolean = fold(from)(p(_).disjunction)

  def all(from: S)(p: A => Boolean): Boolean = fold(from)(p(_).conjunction)

  def compose[B](other: Fold[A, B]): Fold[S, B] = new Fold[S, B] {
    protected def underlyingFold[F[_] : Contravariant : Applicative](from: S)(f: B => F[B]): F[S] =
      self.underlyingFold(from)(other.underlyingFold(_)(f))
  }
}

trait Getting[R, S, A] {

  def getting(lift: A => Constant[R, A])(from: S): Constant[R, S]

}

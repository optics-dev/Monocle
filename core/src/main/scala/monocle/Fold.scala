package monocle

import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._
import scalaz.{ Foldable, Monoid }

trait Fold[S, A] { self =>

  def foldMap[B: Monoid](from: S)(f: A => B): B

  def fold(from: S)(implicit ev: Monoid[A]): A = foldMap(from)(identity)

  def toListOf(from: S): List[A] = foldMap(from)(List(_)).reverse

  def headOption(from: S): Option[A] = foldMap(from)(Option(_).first)

  def exist(from: S)(p: A => Boolean): Boolean = foldMap(from)(p(_).disjunction)

  def all(from: S)(p: A => Boolean): Boolean = foldMap(from)(p(_).conjunction)

  def asFold: Fold[S, A] = self

  def compose[B](other: Fold[A, B]): Fold[S, B] = new Fold[S, B] {
    def foldMap[C: Monoid](from: S)(f: B => C): C = self.foldMap(from)(other.foldMap(_)(f))
  }
}

object Fold {

  def apply[F[_]: Foldable, A]: Fold[F[A], A] = new Fold[F[A], A] {
    def foldMap[B: Monoid](from: F[A])(f: A => B): B = Foldable[F].foldMap(from)(f)
  }

}

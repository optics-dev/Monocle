package monocle

import _root_.scalaz.std.anyVal._
import _root_.scalaz.std.list._
import _root_.scalaz.std.option._
import _root_.scalaz.syntax.std.boolean._
import _root_.scalaz.syntax.std.option._
import _root_.scalaz.{ Foldable, Monoid, Tag }

trait Fold[S, A] { self =>

  def foldMap[B: Monoid](from: S)(f: A => B): B

  def fold(from: S)(implicit ev: Monoid[A]): A = foldMap(from)(identity)

  def getAll(from: S): List[A] = foldMap(from)(List(_))

  def headOption(from: S): Option[A] = Tag.unwrap(foldMap(from)(Option(_).first))

  def exist(from: S)(p: A => Boolean): Boolean = Tag.unwrap(foldMap(from)(p(_).disjunction))

  def all(from: S)(p: A => Boolean): Boolean = Tag.unwrap(foldMap(from)(p(_).conjunction))

  def asFold: Fold[S, A] = self

  /** non overloaded compose function */
  def composeFold[B](other: Fold[A, B]): Fold[S, B] = new Fold[S, B] {
    def foldMap[C: Monoid](from: S)(f: B => C): C = self.foldMap(from)(other.foldMap(_)(f))
  }

  @deprecated("Use composeFold", since = "0.5")
  def compose[B](other: Fold[A, B]): Fold[S, B] = composeFold(other)
}

object Fold {

  def apply[F[_]: Foldable, A]: Fold[F[A], A] = new Fold[F[A], A] {
    def foldMap[B: Monoid](from: F[A])(f: A => B): B = Foldable[F].foldMap(from)(f)
  }

}

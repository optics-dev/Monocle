package monocle

import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._
import scalaz.{ Foldable, Monoid, Tag }

trait Fold[S, A] { self =>

  def foldMap[B: Monoid](from: S)(f: A => B): B

  final def fold(from: S)(implicit ev: Monoid[A]): A = foldMap(from)(identity)

  final def getAll(from: S): List[A] = foldMap(from)(List(_))

  final def headOption(from: S): Option[A] = Tag.unwrap(foldMap(from)(Option(_).first))

  final def exist(from: S)(p: A => Boolean): Boolean = Tag.unwrap(foldMap(from)(p(_).disjunction))

  final def all(from: S)(p: A => Boolean): Boolean = Tag.unwrap(foldMap(from)(p(_).conjunction))

  final def asFold: Fold[S, A] = self

  /** non overloaded compose function */
  final def composeFold[B](other: Fold[A, B]): Fold[S, B] = new Fold[S, B] {
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

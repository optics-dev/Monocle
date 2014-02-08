package monocle

import scalaz.Monoid
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._

trait Fold[S, A] { self =>

  def fold[B: Monoid](from: S)(f: A => B): B

  def simpleFold(from: S)(implicit ev: Monoid[A]): A = fold(from)(identity)

  def toListOf(from: S): List[A] = fold(from)(List(_))

  def headOption(from: S): Option[A] = fold(from)(Option(_).first)

  def exist(from: S)(p: A => Boolean): Boolean = fold(from)(p(_).disjunction)

  def all(from: S)(p: A => Boolean): Boolean = fold(from)(p(_).conjunction)

  def compose[B](other: Fold[A, B]): Fold[S, B] = new Fold[S, B] {
    def fold[C: Monoid](from: S)(f: B => C): C = self.fold(from)(other.fold(_)(f))
  }
}

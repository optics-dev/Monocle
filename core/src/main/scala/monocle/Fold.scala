package monocle

import scalaz.std.anyVal._
import scalaz.syntax.std.boolean._
import scalaz.Maybe._
import scalaz.{Maybe, Foldable, Monoid, Tag, IList}

abstract class Fold[S, A] { self =>

  def foldMap[B: Monoid](s: S)(f: A => B): B

  final def fold(s: S)(implicit ev: Monoid[A]): A = foldMap(s)(identity)
  final def getAll(s: S): IList[A] = foldMap(s)(IList(_))
  final def headMaybe(s: S): Maybe[A] = Tag.unwrap(foldMap(s)(Maybe.just(_).first))
  final def exist(s: S)(p: A => Boolean): Boolean = Tag.unwrap(foldMap(s)(p(_).disjunction))
  final def all(s: S)(p: A => Boolean): Boolean = Tag.unwrap(foldMap(s)(p(_).conjunction))


  final def composeFold[B](other: Fold[A, B]): Fold[S, B] = new Fold[S, B] {
    def foldMap[C: Monoid](s: S)(f: B => C): C = self.foldMap(s)(other.foldMap(_)(f))
  }
  final def composeGetter[C](other: Getter[A, C]): Fold[S, C] = composeFold(other.asFold)
  final def composeTraversal[B, C, D](other: Traversal[A, B, C, D]): Fold[S, C] = composeFold(other.asFold)
  final def composeOptional[B, C, D](other: Optional[A, B, C, D]): Fold[S, C] = composeFold(other.asFold)
  final def composePrism[B, C, D](other: Prism[A, B, C, D]): Fold[S, C] = composeFold(other.asFold)
  final def composeLens[B, C, D](other: Lens[A, B, C, D]): Fold[S, C] = composeFold(other.asFold)
  final def composeIso[B, C, D](other: Iso[A, B, C, D]): Fold[S, C] = composeFold(other.asFold)
}

object Fold {

  def apply[F[_]: Foldable, A]: Fold[F[A], A] = new Fold[F[A], A] {
    def foldMap[B: Monoid](s: F[A])(f: A => B): B = Foldable[F].foldMap(s)(f)
  }

}

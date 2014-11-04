package monocle.syntax

import monocle._
import scalaz.{IList, Maybe, Monoid}

object fold extends FoldSyntax

private[syntax] trait FoldSyntax {
  implicit def toApplyFoldOps[S](value: S): ApplyFoldOps[S] = new ApplyFoldOps(value)
}

final case class ApplyFoldOps[S](s: S) {
  def applyFold[A](fold: Fold[S, A]): ApplyFold[S, A] = new ApplyFold[S, A](s, fold)
}

case class ApplyFold[S, A](s: S, _fold: Fold[S, A]) {
  @inline def foldMap[M: Monoid](f: A => M): M = _fold.foldMap(f)(s)
  @inline def fold(implicit ev: Monoid[A]): A = _fold.fold(s)

  @inline def getAll: IList[A] = _fold.getAll(s)
  @inline def headMaybe: Maybe[A] = _fold.headMaybe(s)

  @inline def exist(p: A => Boolean): Boolean = _fold.exist(p)(s)
  @inline def all(p: A => Boolean): Boolean = _fold.all(p)(s)

  @inline def composeFold[B](other: Fold[A, B]): ApplyFold[S, B] = ApplyFold(s, _fold composeFold other)
  @inline def composeGetter[B](other: Getter[A, B]): ApplyFold[S, B] = ApplyFold(s, _fold composeGetter other)
  @inline def composeTraversal[B, C, D](other: Traversal[A, B, C, D]): ApplyFold[S, C] = ApplyFold(s, _fold composeTraversal other)
  @inline def composeOptional[B, C, D](other: Optional[A, B, C, D]): ApplyFold[S, C] = ApplyFold(s, _fold composeOptional other)
  @inline def composePrism[B, C, D](other: Prism[A, B, C, D]): ApplyFold[S, C] = ApplyFold(s, _fold composePrism other)
  @inline def composeLens[B, C, D](other: Lens[A, B, C, D]): ApplyFold[S, C] = ApplyFold(s, _fold composeLens other)
  @inline def composeIso[B, C, D](other: Iso[A, B, C, D]): ApplyFold[S, C] = ApplyFold(s, _fold composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[B, C, D](other: Traversal[A, B, C, D]): ApplyFold[S, C] = composeTraversal(other)
  /** alias to composeOptional */
  @inline def ^|-?[B, C, D](other: Optional[A, B, C, D]): ApplyFold[S, C] = composeOptional(other)
  /** alias to composePrism */
  @inline def ^<-?[B, C, D](other: Prism[A, B, C, D]): ApplyFold[S, C] = composePrism(other)
  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: Lens[A, B, C, D]): ApplyFold[S, C] = composeLens(other)
  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: Iso[A, B, C, D]): ApplyFold[S, C] = composeIso(other)
}
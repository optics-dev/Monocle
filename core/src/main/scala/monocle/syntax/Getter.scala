package monocle.syntax

import monocle._

object getter extends GetterSyntax

private[syntax] trait GetterSyntax {
  implicit def toApplyGetterOps[S](value: S): ApplyGetterOps[S] = new ApplyGetterOps(value)
}

final case class ApplyGetterOps[S](s: S) {
  def applyGetter[A](getter: Getter[S, A]): ApplyGetter[S, A] = new ApplyGetter[S, A](s, getter)
}

final case class ApplyGetter[S, A](s: S, getter: Getter[S, A]){
  def get: A = getter.get(s)

  def composeFold[B](other: Fold[A, B]): ApplyFold[S, B] = ApplyFold(s, getter composeFold other)
  def composeGetter[B](other: Getter[A, B]): ApplyGetter[S, B] = ApplyGetter(s, getter composeGetter other)
  def composeLens[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C] = ApplyGetter(s, getter composeLens other)
  def composeIso[B, C, D](other: Iso[A, B, C, D]): ApplyGetter[S, C] = ApplyGetter(s, getter composeIso other)
}
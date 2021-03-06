package monocle.syntax

import monocle._

object apply extends ApplySyntax

trait ApplySyntax {
  implicit def toApplyFoldOps[S](value: S): ApplyFoldOps[S] =
    new ApplyFoldOps(value)
  implicit def toApplyGetterOps[S](value: S): ApplyGetterOps[S] =
    new ApplyGetterOps(value)
  implicit def toApplyIsoOps[S](value: S): ApplyIsoOps[S] =
    new ApplyIsoOps(value)
  implicit def toApplyLensOps[S](value: S): ApplyLensOps[S] =
    new ApplyLensOps(value)
  implicit def toApplyOptionalOps[S](value: S): ApplyOptionalOps[S] =
    new ApplyOptionalOps(value)
  implicit def toApplyPrismOps[S](value: S): ApplyPrismOps[S] =
    new ApplyPrismOps(value)
  implicit def toApplySetterOps[S](value: S): ApplySetterOps[S] =
    new ApplySetterOps(value)
  implicit def toApplyTraversalOps[S](value: S): ApplyTraversalOps[S] =
    new ApplyTraversalOps(value)
}

final case class ApplyFoldOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyFold[A](fold: Fold[S, A]): ApplyFold[S, A] =
    new ApplyFold[S, A](s, fold)
}

final case class ApplyGetterOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyGetter[A](getter: Getter[S, A]): ApplyGetter[S, A] =
    new ApplyGetter[S, A](s, getter)
}

final case class ApplyIsoOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyIso[T, A, B](iso: PIso[S, T, A, B]): ApplyPIso[S, T, A, B] =
    ApplyPIso[S, T, A, B](s, iso)

  /** alias to applyIso */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &<->[T, A, B](iso: PIso[S, T, A, B]): ApplyPIso[S, T, A, B] =
    applyIso(iso)
}

final case class ApplyLensOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyLens[T, A, B](lens: PLens[S, T, A, B]): ApplyPLens[S, T, A, B] =
    ApplyPLens[S, T, A, B](s, lens)

  /** alias to applyLens */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &|->[T, A, B](lens: PLens[S, T, A, B]): ApplyPLens[S, T, A, B] =
    applyLens(lens)
}

final case class ApplyOptionalOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyOptional[T, A, B](optional: POptional[S, T, A, B]): ApplyPOptional[S, T, A, B] =
    ApplyPOptional[S, T, A, B](s, optional)

  /** alias to applyOptional */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &|-?[T, A, B](optional: POptional[S, T, A, B]): ApplyPOptional[S, T, A, B] =
    applyOptional(optional)
}

final case class ApplyPrismOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyPrism[T, A, B](prism: PPrism[S, T, A, B]): ApplyPPrism[S, T, A, B] =
    ApplyPPrism[S, T, A, B](s, prism)

  /** alias to applyPrism */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &<-?[T, A, B](prism: PPrism[S, T, A, B]): ApplyPPrism[S, T, A, B] =
    applyPrism(prism)
}

final case class ApplySetterOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applySetter[T, A, B](setter: PSetter[S, T, A, B]): ApplyPSetter[S, T, A, B] =
    new ApplyPSetter[S, T, A, B](s, setter)
}

final case class ApplyTraversalOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyTraversal[T, A, B](traversal: PTraversal[S, T, A, B]): ApplyPTraversal[S, T, A, B] =
    ApplyPTraversal[S, T, A, B](s, traversal)

  /** alias to applyTraversal */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &|->>[T, A, B](traversal: PTraversal[S, T, A, B]): ApplyPTraversal[S, T, A, B] =
    applyTraversal(traversal)
}

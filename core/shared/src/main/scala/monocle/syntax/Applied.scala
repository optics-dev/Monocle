package monocle.syntax

import monocle._

@deprecated("use monocle.syntax.applied", since = "3.0.0-M4")
object apply extends AppliedSyntax

object applied extends AppliedSyntax

trait AppliedSyntax {
  implicit def toAppliedFoldOps[S](value: S): AppliedFoldOps[S] =
    new AppliedFoldOps(value)
  implicit def toAppliedGetterOps[S](value: S): AppliedGetterOps[S] =
    new AppliedGetterOps(value)
  implicit def toAppliedIsoOps[S](value: S): AppliedIsoOps[S] =
    new AppliedIsoOps(value)
  implicit def toAppliedLensOps[S](value: S): AppliedLensOps[S] =
    new AppliedLensOps(value)
  implicit def toAppliedOptionalOps[S](value: S): AppliedOptionalOps[S] =
    new AppliedOptionalOps(value)
  implicit def toAppliedPrismOps[S](value: S): AppliedPrismOps[S] =
    new AppliedPrismOps(value)
  implicit def toAppliedSetterOps[S](value: S): AppliedSetterOps[S] =
    new AppliedSetterOps(value)
  implicit def toAppliedTraversalOps[S](value: S): AppliedTraversalOps[S] =
    new AppliedTraversalOps(value)
}

final case class AppliedFoldOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyFold[A](fold: Fold[S, A]): AppliedFold[S, A] =
    new AppliedFold[S, A](s, fold)
}

final case class AppliedGetterOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyGetter[A](getter: Getter[S, A]): AppliedGetter[S, A] =
    new AppliedGetter[S, A](s, getter)
}

final case class AppliedIsoOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyIso[T, A, B](iso: PIso[S, T, A, B]): AppliedPIso[S, T, A, B] =
    AppliedPIso[S, T, A, B](s, iso)

  /** alias to applyIso */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &<->[T, A, B](iso: PIso[S, T, A, B]): AppliedPIso[S, T, A, B] =
    applyIso(iso)
}

final case class AppliedLensOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyLens[T, A, B](lens: PLens[S, T, A, B]): AppliedPLens[S, T, A, B] =
    AppliedPLens[S, T, A, B](s, lens)

  /** alias to applyLens */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &|->[T, A, B](lens: PLens[S, T, A, B]): AppliedPLens[S, T, A, B] =
    applyLens(lens)
}

final case class AppliedOptionalOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyOptional[T, A, B](optional: POptional[S, T, A, B]): AppliedPOptional[S, T, A, B] =
    AppliedPOptional[S, T, A, B](s, optional)

  /** alias to applyOptional */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &|-?[T, A, B](optional: POptional[S, T, A, B]): AppliedPOptional[S, T, A, B] =
    applyOptional(optional)
}

final case class AppliedPrismOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyPrism[T, A, B](prism: PPrism[S, T, A, B]): AppliedPPrism[S, T, A, B] =
    AppliedPPrism[S, T, A, B](s, prism)

  /** alias to applyPrism */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &<-?[T, A, B](prism: PPrism[S, T, A, B]): AppliedPPrism[S, T, A, B] =
    applyPrism(prism)
}

final case class AppliedSetterOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applySetter[T, A, B](setter: PSetter[S, T, A, B]): AppliedPSetter[S, T, A, B] =
    new AppliedPSetter[S, T, A, B](s, setter)
}

final case class AppliedTraversalOps[S](private val s: S) extends AnyVal {
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def applyTraversal[T, A, B](traversal: PTraversal[S, T, A, B]): AppliedPTraversal[S, T, A, B] =
    AppliedPTraversal[S, T, A, B](s, traversal)

  /** alias to applyTraversal */
  @deprecated("use focus().andThen", since = "3.0.0-M1")
  def &|->>[T, A, B](traversal: PTraversal[S, T, A, B]): AppliedPTraversal[S, T, A, B] =
    applyTraversal(traversal)
}

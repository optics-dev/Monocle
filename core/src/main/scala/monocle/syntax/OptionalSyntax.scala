package monocle.syntax

import monocle.{Traversal, Optional}


private[syntax] trait OptionalSyntax {
  implicit def toOptionalOps[S, T, A, B](Optional: Optional[S, T, A, B]): OptionalOps[S, T, A, B] = new OptionalOps(Optional)

  implicit def tolApplyOptionalOps[S](value: S): lyApplyOptionalOps[S] = new lyApplyOptionalOps(value)
}

private[syntax] final class OptionalOps[S, T, A, B](val self: Optional[S, T, A, B]) {
  def <-?[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = self compose other
}

private[syntax] trait ApplyOptional[S, T, A, B] extends ApplyTraversal[S, T, A, B]  { self =>
  def _optional: Optional[S, T, A, B]

  def _traversal: Traversal[S, T, A, B] = _optional

  def getOption: Option[A] = _optional.getOption(from)

  def composeOptional[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = new ApplyOptional[S, T, C, D] {
    val _optional: Optional[S, T, C, D] = self._optional compose other
    val from: S = self.from
  }

  /** Alias to composeOptional */
  def |-?[C, D](other: Optional[A, B, C, D]): ApplyOptional[S, T, C, D] = composeOptional(other)
}

private[syntax] final class lyApplyOptionalOps[S](value: S) {
  def applyOptional[T, A, B](Optional: Optional[S, T, A, B]): ApplyOptional[S, T, A, B] = new ApplyOptional[S, T, A, B] {
    val from: S = value
    def _optional: Optional[S, T, A, B] = Optional
  }

  def |-?[T, A, B](Optional: Optional[S, T, A, B]): ApplyOptional[S, T, A, B] = applyOptional(Optional)
}
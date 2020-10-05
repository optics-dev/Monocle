package monocle.syntax

import monocle._
import cats.{Applicative, Functor, Monoid}
import monocle.function.Each

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
  def applyFold[A](fold: Fold[S, A]): ApplyFold[S, A] =
    new ApplyFold[S, A](s, fold)
}

final case class ApplyGetterOps[S](private val s: S) extends AnyVal {
  def applyGetter[A](getter: Getter[S, A]): ApplyGetter[S, A] =
    new ApplyGetter[S, A](s, getter)
}

final case class ApplyIsoOps[S](private val s: S) extends AnyVal {
  @inline def applyIso[T, A, B](iso: PIso[S, T, A, B]): ApplyIso[S, T, A, B] =
    ApplyIso[S, T, A, B](s, iso)

  /** alias to applyIso */
  @inline def &<->[T, A, B](iso: PIso[S, T, A, B]): ApplyIso[S, T, A, B] =
    applyIso(iso)
}

final case class ApplyLensOps[S](private val s: S) extends AnyVal {
  def applyLens[T, A, B](lens: PLens[S, T, A, B]): ApplyLens[S, T, A, B] =
    ApplyLens[S, T, A, B](s, lens)

  /** alias to applyLens */
  def &|->[T, A, B](lens: PLens[S, T, A, B]): ApplyLens[S, T, A, B] =
    applyLens(lens)
}

final case class ApplyOptionalOps[S](private val s: S) extends AnyVal {
  def applyOptional[T, A, B](optional: POptional[S, T, A, B]): ApplyOptional[S, T, A, B] =
    ApplyOptional[S, T, A, B](s, optional)

  /** alias to applyOptional */
  def &|-?[T, A, B](optional: POptional[S, T, A, B]): ApplyOptional[S, T, A, B] =
    applyOptional(optional)
}

final case class ApplyPrismOps[S](private val s: S) extends AnyVal {
  def applyPrism[T, A, B](prism: PPrism[S, T, A, B]): ApplyPrism[S, T, A, B] =
    ApplyPrism[S, T, A, B](s, prism)

  /** alias to applyPrism */
  def &<-?[T, A, B](prism: PPrism[S, T, A, B]): ApplyPrism[S, T, A, B] =
    applyPrism(prism)
}

final case class ApplySetterOps[S](private val s: S) extends AnyVal {
  def applySetter[T, A, B](setter: PSetter[S, T, A, B]): ApplySetter[S, T, A, B] =
    new ApplySetter[S, T, A, B](s, setter)
}

final case class ApplyTraversalOps[S](private val s: S) extends AnyVal {
  def applyTraversal[T, A, B](traversal: PTraversal[S, T, A, B]): ApplyTraversal[S, T, A, B] =
    ApplyTraversal[S, T, A, B](s, traversal)

  /** alias to applyTraversal */
  def &|->>[T, A, B](traversal: PTraversal[S, T, A, B]): ApplyTraversal[S, T, A, B] =
    applyTraversal(traversal)
}

case class ApplyFold[S, A](s: S, _fold: Fold[S, A]) {
  @inline def foldMap[M: Monoid](f: A => M): M = _fold.foldMap(f)(s)

  @inline def getAll: List[A]                       = _fold.getAll(s)
  @inline def find(p: A => Boolean): S => Option[A] = _fold.find(p)
  @inline def headOption: Option[A]                 = _fold.headOption(s)
  @inline def lastOption: Option[A]                 = _fold.lastOption(s)
  @inline def exist(p: A => Boolean): Boolean       = _fold.exist(p)(s)
  @inline def all(p: A => Boolean): Boolean         = _fold.all(p)(s)
  @inline def length: Int                           = _fold.length(s)
  @inline def isEmpty: Boolean                      = _fold.isEmpty(s)
  @inline def nonEmpty: Boolean                     = _fold.nonEmpty(s)

  def some[A1](implicit ev1: A =:= Option[A1]): ApplyFold[S, A1] =
    adapt[Option[A1]] composePrism (std.option.pSome)

  private def adapt[A1](implicit evA: A =:= A1): ApplyFold[S, A1] =
    evA.substituteCo[ApplyFold[S, *]](this)

  @inline def composeFold[B](other: Fold[A, B]): ApplyFold[S, B] =
    ApplyFold(s, _fold composeFold other)
  @inline def composeGetter[B](other: Getter[A, B]): ApplyFold[S, B] =
    ApplyFold(s, _fold composeGetter other)
  @inline def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeTraversal other)
  @inline def composeOptional[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeOptional other)
  @inline def composePrism[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composePrism other)
  @inline def composeLens[B, C, D](other: PLens[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeLens other)
  @inline def composeIso[B, C, D](other: PIso[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, _fold composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: PLens[A, B, C, D]): ApplyFold[S, C] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: PIso[A, B, C, D]): ApplyFold[S, C] =
    composeIso(other)
}

final case class ApplyGetter[S, A](s: S, getter: Getter[S, A]) {
  @inline def get: A                                = getter.get(s)
  @inline def exist(p: A => Boolean): S => Boolean  = getter.exist(p)
  @inline def find(p: A => Boolean): S => Option[A] = getter.find(p)

  def some[A1](implicit ev1: A =:= Option[A1]): ApplyFold[S, A1] =
    adapt[Option[A1]] composePrism (std.option.pSome)

  private def adapt[A1](implicit evA: A =:= A1): ApplyGetter[S, A1] =
    evA.substituteCo[ApplyGetter[S, *]](this)

  @inline def composeFold[B](other: Fold[A, B]): ApplyFold[S, B] =
    ApplyFold(s, getter composeFold other)
  @inline def composeGetter[B](other: Getter[A, B]): ApplyGetter[S, B] =
    ApplyGetter(s, getter composeGetter other)
  @inline def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, getter composeTraversal other)
  @inline def composeOptional[B, C, D](other: POptional[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, getter composeOptional other)
  @inline def composePrism[B, C, D](other: PPrism[A, B, C, D]): ApplyFold[S, C] =
    ApplyFold(s, getter composePrism other)
  @inline def composeLens[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C] =
    ApplyGetter(s, getter composeLens other)
  @inline def composeIso[B, C, D](other: PIso[A, B, C, D]): ApplyGetter[S, C] =
    ApplyGetter(s, getter composeIso other)

  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: PLens[A, B, C, D]): ApplyGetter[S, C] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: PIso[A, B, C, D]): ApplyGetter[S, C] =
    composeIso(other)
}

final case class ApplyIso[S, T, A, B](s: S, iso: PIso[S, T, A, B]) {
  @inline def get: A                                     = iso.get(s)
  @inline def set(b: B): T                               = iso.set(b)(s)
  @inline def modify(f: A => B): T                       = iso.modify(f)(s)
  @inline def modifyF[F[_]: Functor](f: A => F[B]): F[T] = iso.modifyF(f)(s)
  @inline def exist(p: A => Boolean): S => Boolean       = iso.exist(p)
  @inline def find(p: A => Boolean): S => Option[A]      = iso.find(p)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyIso[S, T, A1, B1] =
    evB.substituteCo[ApplyIso[S, T, A1, *]](evA.substituteCo[ApplyIso[S, T, *, B]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, iso composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, iso composeFold other)
  @inline def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C] =
    ApplyGetter(s, iso composeGetter other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, iso composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, iso composeOptional other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyPrism[S, T, C, D] =
    ApplyPrism(s, iso composePrism other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] =
    ApplyLens(s, iso composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyIso[S, T, C, D] =
    ApplyIso(s, iso composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyPrism[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyIso[S, T, C, D] =
    composeIso(other)
}

final case class ApplyLens[S, T, A, B](s: S, lens: PLens[S, T, A, B]) {
  @inline def get: A                                     = lens.get(s)
  @inline def set(b: B): T                               = lens.set(b)(s)
  @inline def modify(f: A => B): T                       = lens.modify(f)(s)
  @inline def modifyF[F[_]: Functor](f: A => F[B]): F[T] = lens.modifyF(f)(s)
  @inline def exist(p: A => Boolean): S => Boolean       = lens.exist(p)
  @inline def find(p: A => Boolean): S => Option[A]      = lens.find(p)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyOptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  def each[C](implicit evTS: T =:= S, evBA: B =:= A, evEach: Each[A, C]): ApplyTraversal[S, S, C, C] =
    mono composeTraversal evEach.each

  private def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyLens[S, T, A1, B1] =
    evB.substituteCo[ApplyLens[S, T, A1, *]](evA.substituteCo[ApplyLens[S, T, *, B]](this))

  def mono(implicit evTS: T =:= S, evBA: B =:= A): ApplyLens[S, S, A, A] =
    evTS.substituteCo[ApplyLens[S, *, A, A]](evBA.substituteCo[ApplyLens[S, T, A, *]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, lens composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, lens composeFold other)
  @inline def composeGetter[C](other: Getter[A, C]): ApplyGetter[S, C] =
    ApplyGetter(s, lens composeGetter other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, lens composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, lens composeOptional other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, lens composePrism other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] =
    ApplyLens(s, lens composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D] =
    ApplyLens(s, lens composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyLens[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyLens[S, T, C, D] =
    composeIso(other)
}

final case class ApplyOptional[S, T, A, B](s: S, optional: POptional[S, T, A, B]) {
  @inline def getOption: Option[A] = optional.getOption(s)

  @inline def isEmpty(s: S): Boolean             = optional.isEmpty(s)
  @inline def nonEmpty(s: S): Boolean            = optional.nonEmpty(s)
  @inline def all(p: A => Boolean): S => Boolean = optional.all(p)

  @inline def exist(p: A => Boolean): S => Boolean  = optional.exist(p)
  @inline def find(p: A => Boolean): S => Option[A] = optional.find(p)

  @inline def modify(f: A => B): T = optional.modify(f)(s)
  @inline def modifyF[F[_]: Applicative](f: A => F[B]): F[T] =
    optional.modifyF(f)(s)
  @inline def modifyOption(f: A => B): Option[T] = optional.modifyOption(f)(s)

  @inline def set(b: B): T               = optional.set(b)(s)
  @inline def setOption(b: B): Option[T] = optional.setOption(b)(s)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyOptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyOptional[S, T, A1, B1] =
    evB.substituteCo[ApplyOptional[S, T, A1, *]](evA.substituteCo[ApplyOptional[S, T, *, B]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, optional composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, optional composeFold other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, optional composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composeOptional other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composePrism other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, optional composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeIso(other)
}

final case class ApplyPrism[S, T, A, B](s: S, prism: PPrism[S, T, A, B]) {
  @inline def getOption: Option[A] = prism.getOption(s)

  @inline def modify(f: A => B): T = prism.modify(f)(s)
  @inline def modifyF[F[_]: Applicative](f: A => F[B]): F[T] =
    prism.modifyF(f)(s)
  @inline def modifyOption(f: A => B): Option[T] = prism.modifyOption(f)(s)

  @inline def set(b: B): T                     = prism.set(b)(s)
  @inline def setOption(b: B): Option[T]       = prism.setOption(b)(s)
  @inline def isEmpty: Boolean                 = prism.isEmpty(s)
  @inline def nonEmpty: Boolean                = prism.nonEmpty(s)
  @inline def find(p: A => Boolean): Option[A] = prism.find(p)(s)
  @inline def exist(p: A => Boolean): Boolean  = prism.exist(p)(s)
  @inline def all(p: A => Boolean): Boolean    = prism.all(p)(s)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyPrism[S, T, A1, B1] =
    evB.substituteCo[ApplyPrism[S, T, A1, *]](evA.substituteCo[ApplyPrism[S, T, *, B]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, prism composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, prism composeFold other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, prism composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, prism composeOptional other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] =
    ApplyOptional(s, prism composeLens other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyPrism[S, T, C, D] =
    ApplyPrism(s, prism composePrism other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyPrism[S, T, C, D] =
    ApplyPrism(s, prism composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyPrism[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyOptional[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyPrism[S, T, C, D] =
    composeIso(other)
}

final case class ApplySetter[S, T, A, B](s: S, setter: PSetter[S, T, A, B]) {
  @inline def set(b: B): T         = setter.set(b)(s)
  @inline def modify(f: A => B): T = setter.modify(f)(s)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplySetter[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplySetter[S, T, A1, B1] =
    evB.substituteCo[ApplySetter[S, T, A1, *]](evA.substituteCo[ApplySetter[S, T, *, B]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, setter composeSetter other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, setter composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, setter composeOptional other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, setter composePrism other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, setter composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, setter composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplySetter[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplySetter[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplySetter[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplySetter[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplySetter[S, T, C, D] =
    composeIso(other)
}

final case class ApplyTraversal[S, T, A, B](s: S, traversal: PTraversal[S, T, A, B]) {
  @inline def getAll: List[A]       = traversal.getAll(s)
  @inline def headOption: Option[A] = traversal.headOption(s)
  @inline def lastOption: Option[A] = traversal.lastOption(s)

  @inline def set(b: B): T         = traversal.set(b)(s)
  @inline def modify(f: A => B): T = traversal.modify(f)(s)
  @inline def modifyF[F[_]: Applicative](f: A => F[B]): F[T] =
    traversal.modifyF(f)(s)

  @inline def find(p: A => Boolean): S => Option[A] = traversal.find(p)
  @inline def exist(p: A => Boolean): S => Boolean  = traversal.exist(p)
  @inline def all(p: A => Boolean): S => Boolean    = traversal.all(p)
  @inline def isEmpty(s: S): Boolean                = traversal.isEmpty(s)
  @inline def nonEmpty(s: S): Boolean               = traversal.nonEmpty(s)

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): ApplyTraversal[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): ApplyTraversal[S, T, A1, B1] =
    evB.substituteCo[ApplyTraversal[S, T, A1, *]](evA.substituteCo[ApplyTraversal[S, T, *, B]](this))

  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): ApplySetter[S, T, C, D] =
    ApplySetter(s, traversal composeSetter other)
  @inline def composeFold[C](other: Fold[A, C]): ApplyFold[S, C] =
    ApplyFold(s, traversal composeFold other)
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal composeTraversal other)
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal composeOptional other)
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal composePrism other)
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal composeLens other)
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    ApplyTraversal(s, traversal composeIso other)

  /** alias to composeTraversal */
  @inline def ^|->>[C, D](other: PTraversal[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[C, D](other: POptional[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[C, D](other: PPrism[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[C, D](other: PLens[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[C, D](other: PIso[A, B, C, D]): ApplyTraversal[S, T, C, D] =
    composeIso(other)
}

package monocle

import cats.{Applicative, Eq, Functor, Monoid}
import cats.arrow.Choice
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}

/** A [[Lens]] represent the relations between a case class and one of its field.
  *
  * @see [[monocle.law.LensLaws]] to see the expected behaviour for a Lens.
  *
  * @tparam SourceIn the source of a [[PLens]]
  * @tparam SourceOut the modified source of a [[PLens]]
  * @tparam TargetIn the target of a [[PLens]]
  * @tparam TargetOut the modified target of a [[PLens]]
  */
abstract class PLens[SourceIn, SourceOut, TargetIn, TargetOut] extends Serializable { self =>

  /** extract the target from a source */
  def get(source: SourceIn): TargetIn

  /** replace the target */
  def replace(target: TargetOut): SourceIn => SourceOut

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: TargetOut): SourceIn => SourceOut = replace(b)

  /** modify the target using a function with effects */
  def modifyF[Effect[_]: Functor](f: TargetIn => Effect[TargetOut])(s: SourceIn): Effect[SourceOut]

  /** modify the target using a function. 
    * This is equivalent to: {{
    *   val target    = get(source)
    *   val newTarget = update(target)
    *   replace(newTarget)(source)
    * }}
    * */
  def modify(update: TargetIn => TargetOut): SourceIn => SourceOut

  /** find if the target satisfies a predicate */
  final def find(predicate: TargetIn => Boolean): SourceIn => Option[TargetIn] =
    s => Some(get(s)).filter(predicate)

  /** check if the target satisfies a predicate */
  final def exist(predicate: TargetIn => Boolean): SourceIn => Boolean =
    predicate compose get

  /** join two Lenses with the same targets */
  final def choice[S1, T1](other: PLens[S1, T1, TargetIn, TargetOut]): PLens[Either[SourceIn, S1], Either[SourceOut, T1], TargetIn, TargetOut] =
    PLens[Either[SourceIn, S1], Either[SourceOut, T1], TargetIn, TargetOut](_.fold(self.get, other.get))(b =>
      _.bimap(self.replace(b), other.replace(b))
    )

  /** pair two disjoint Lenses */
  final def split[S1, T1, A1, B1](other: PLens[S1, T1, A1, B1]): PLens[(SourceIn, S1), (SourceOut, T1), (TargetIn, A1), (TargetOut, B1)] =
    PLens[(SourceIn, S1), (SourceOut, T1), (TargetIn, A1), (TargetOut, B1)] { case (s, s1) =>
      (self.get(s), other.get(s1))
    } {
      case (b, b1) => { case (s, s1) =>
        (self.replace(b)(s), other.replace(b1)(s1))
      }
    }

  final def first[C]: PLens[(SourceIn, C), (SourceOut, C), (TargetIn, C), (TargetOut, C)] =
    PLens[(SourceIn, C), (SourceOut, C), (TargetIn, C), (TargetOut, C)] { case (s, c) =>
      (get(s), c)
    } {
      case (b, c) => { case (s, _) =>
        (replace(b)(s), c)
      }
    }

  final def second[C]: PLens[(C, SourceIn), (C, SourceOut), (C, TargetIn), (C, TargetOut)] =
    PLens[(C, SourceIn), (C, SourceOut), (C, TargetIn), (C, TargetOut)] { case (c, s) =>
      (c, get(s))
    } {
      case (c, b) => { case (_, s) =>
        (c, replace(b)(s))
      }
    }

  def some[A1, B1](implicit targetInIsOption: TargetIn =:= Option[A1], targetOutIsOption: TargetOut =:= Option[B1]): POptional[SourceIn, SourceOut, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  private[monocle] def adapt[A1, B1](implicit evA: TargetIn =:= A1, evB: TargetOut =:= B1): PLens[SourceIn, SourceOut, A1, B1] =
    evB.substituteCo[PLens[SourceIn, SourceOut, A1, *]](evA.substituteCo[PLens[SourceIn, SourceOut, *, TargetOut]](this))

  /** compose with a [[Fold]] */
  final def andThen[C](other: Fold[TargetIn, C]): Fold[SourceIn, C] =
    asFold.andThen(other)

  /** compose with a [[Getter]] */
  final def andThen[C](other: Getter[TargetIn, C]): Getter[SourceIn, C] =
    asGetter.andThen(other)

  /** compose with a [[PSetter]] */
  final def andThen[NextIn, NextOut](other: PSetter[TargetIn, TargetOut, NextIn, NextOut]): PSetter[SourceIn, SourceOut, NextIn, NextOut] =
    asSetter.andThen(other)

  /** compose with a [[PTraversal]] */
  final def andThen[NextIn, NextOut](other: PTraversal[TargetIn, TargetOut, NextIn, NextOut]): PTraversal[SourceIn, SourceOut, NextIn, NextOut] =
    asTraversal.andThen(other)

  /** compose with an [[POptional]] */
  final def andThen[NextIn, NextOut](other: POptional[TargetIn, TargetOut, NextIn, NextOut]): POptional[SourceIn, SourceOut, NextIn, NextOut] =
    asOptional.andThen(other)

  /** compose with a [[PPrism]] */
  final def andThen[NextIn, NextOut](other: PPrism[TargetIn, TargetOut, NextIn, NextOut]): POptional[SourceIn, SourceOut, NextIn, NextOut] =
    asOptional.andThen(other)

  /** compose with a [[PLens]] */
  final def andThen[NextIn, NextOut](other: PLens[TargetIn, TargetOut, NextIn, NextOut]): PLens[SourceIn, SourceOut, NextIn, NextOut] =
    new PLens[SourceIn, SourceOut, NextIn, NextOut] {
      def get(source: SourceIn): NextIn =
        other.get(self.get(source))

      def replace(next: NextOut): SourceIn => SourceOut =
        self.modify(other.replace(next))

      def modifyF[F[_]: Functor](update: NextIn => F[NextOut])(s: SourceIn): F[SourceOut] =
        self.modifyF(other.modifyF(update))(s)

      def modify(update: NextIn => NextOut): SourceIn => SourceOut =
        self.modify(other.modify(update))
    }

  /** compose with an [[PIso]] */
  final def andThen[NextIn, NextOut](other: PIso[TargetIn, TargetOut, NextIn, NextOut]): PLens[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other.asLens)

  /** compose with a [[Fold]] */
  final def composeFold[C](other: Fold[TargetIn, C]): Fold[SourceIn, C] =
    andThen(other)

  /** Compose with a function */
  def to[Next](zoom: TargetIn => Next): Getter[SourceIn, Next] = andThen(Getter(zoom))

  /** compose with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeGetter[C](other: Getter[TargetIn, C]): Getter[SourceIn, C] =
    andThen(other)

  /** compose  with a [[PSetter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeSetter[NextIn, NextOut](other: PSetter[TargetIn, TargetOut, NextIn, NextOut]): PSetter[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** compose with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeTraversal[NextIn, NextOut](other: PTraversal[TargetIn, TargetOut, NextIn, NextOut]): PTraversal[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** compose with an [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeOptional[NextIn, NextOut](other: POptional[TargetIn, TargetOut, NextIn, NextOut]): POptional[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** compose with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composePrism[NextIn, NextOut](other: PPrism[TargetIn, TargetOut, NextIn, NextOut]): POptional[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** compose with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeLens[NextIn, NextOut](other: PLens[TargetIn, TargetOut, NextIn, NextOut]): PLens[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** compose with an [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeIso[NextIn, NextOut](other: PIso[TargetIn, TargetOut, NextIn, NextOut]): PLens[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** *****************************************
    */
  /** Experimental aliases of compose methods */
  /** *****************************************
    */
  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|->>[NextIn, NextOut](other: PTraversal[TargetIn, TargetOut, NextIn, NextOut]): PTraversal[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|-?[NextIn, NextOut](other: POptional[TargetIn, TargetOut, NextIn, NextOut]): POptional[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^<-?[NextIn, NextOut](other: PPrism[TargetIn, TargetOut, NextIn, NextOut]): POptional[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|->[NextIn, NextOut](other: PLens[TargetIn, TargetOut, NextIn, NextOut]): PLens[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^<->[NextIn, NextOut](other: PIso[TargetIn, TargetOut, NextIn, NextOut]): PLens[SourceIn, SourceOut, NextIn, NextOut] =
    andThen(other)

  /** *********************************************************************************************
    */
  /** Transformation methods to view a [[PLens]] as another Optics */
  /** *********************************************************************************************
    */
  /** upcast to a [[Fold]] */
  final def asFold: Fold[SourceIn, TargetIn] =
    new Fold[SourceIn, TargetIn] {
      def foldMap[M: Monoid](f: TargetIn => M)(source: SourceIn): M =
        f(get(source))
    }

  /** upcast to a [[Getter]] */
  final def asGetter: Getter[SourceIn, TargetIn] =
    (source: SourceIn) => self.get(source)

  /** upcast to a [[PSetter]] */
  final def asSetter: PSetter[SourceIn, SourceOut, TargetIn, TargetOut] =
    new PSetter[SourceIn, SourceOut, TargetIn, TargetOut] {
      def modify(update: TargetIn => TargetOut): SourceIn => SourceOut =
        self.modify(update)

      def replace(target: TargetOut): SourceIn => SourceOut =
        self.replace(target)
    }

  /** upcast to a [[PTraversal]] */
  final def asTraversal: PTraversal[SourceIn, SourceOut, TargetIn, TargetOut] =
    new PTraversal[SourceIn, SourceOut, TargetIn, TargetOut] {
      def modifyF[F[_]: Applicative](update: TargetIn => F[TargetOut])(s: SourceIn): F[SourceOut] =
        self.modifyF(update)(s)
    }

  /** upcast to an [[POptional]] */
  final def asOptional: POptional[SourceIn, SourceOut, TargetIn, TargetOut] =
    new POptional[SourceIn, SourceOut, TargetIn, TargetOut] {
      def getOrModify(source: SourceIn): Either[SourceOut, TargetIn] =
        Either.right(get(source))

      def replace(target: TargetOut): SourceIn => SourceOut =
        self.replace(target)

      def getOption(source: SourceIn): Option[TargetIn] =
        Some(self.get(source))

      def modify(update: TargetIn => TargetOut): SourceIn => SourceOut =
        self.modify(update)

      def modifyF[F[_]: Applicative](update: TargetIn => F[TargetOut])(source: SourceIn): F[SourceOut] =
        self.modifyF(update)(source)
    }
}

object PLens extends LensInstances {
  def id[Source, Target]: PLens[Source, Target, Source, Target] =
    PIso.id[Source, Target].asLens

  def codiagonal[Source, Target]: PLens[Either[Source, Source], Either[Target, Target], Source, Target] =
    PLens[Either[Source, Source], Either[Target, Target], Source, Target](
      _.fold(identity, identity)
    )(target => _.bimap(_ => target, _ => target))

  /** create a [[PLens]] using a pair of functions: one to get the target, one to replace the target.
    * @see macro module for methods generating [[PLens]] with less boiler plate
    */
  def apply[SourceIn, SourceOut, TargetIn, TargetOut](_get: SourceIn => TargetIn)(_replace: TargetOut => SourceIn => SourceOut): PLens[SourceIn, SourceOut, TargetIn, TargetOut] =
    new PLens[SourceIn, SourceOut, TargetIn, TargetOut] {
      def get(source: SourceIn): TargetIn =
        _get(source)

      def replace(target: TargetOut): SourceIn => SourceOut =
        _replace(target)

      def modifyF[F[_]: Functor](update: TargetIn => F[TargetOut])(s: SourceIn): F[SourceOut] =
        Functor[F].map(update(_get(s)))(_replace(_)(s))

      def modify(update: TargetIn => TargetOut): SourceIn => SourceOut =
        s => _replace(update(_get(s)))(s)
    }

  implicit def lensSyntax[Source, Target](self: Lens[Source, Target]): LensSyntax[Source, Target] =
    new LensSyntax(self)
}

object Lens {
  def id[Source]: Lens[Source, Source] =
    Iso.id[Source].asLens

  def codiagonal[Source]: Lens[Either[Source, Source], Source] =
    PLens.codiagonal

  /** create a [[Lens]] using a pair of functions: one to get the target, one to replace the target.
    */
  def apply[Source, Target](get: Source => Target)(replace: Target => Source => Source): Lens[Source, Target] =
    PLens(get)(replace)
}

sealed abstract class LensInstances {
  implicit val lensChoice: Choice[Lens] = new Choice[Lens] {
    def choice[Source, OtherSource, Target](f: Lens[Source, Target], g: Lens[OtherSource, Target]): Lens[Either[Source, OtherSource], Target] =
      f choice g

    def id[Source]: Lens[Source, Source] =
      Lens.id

    def compose[Source, Target, Next](f: Lens[Target, Next], g: Lens[Source, Target]): Lens[Source, Next] =
      g.andThen(f)
  }
}

/** Extension methods for Lens */
final case class LensSyntax[Source, Target](private val self: Lens[Source, Target]) extends AnyVal {
  def each[Next](implicit evEach: Each[Target, Next]): Traversal[Source, Next] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: Target => Boolean): Optional[Source, Target] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[Key, Next](predicate: Key => Boolean)(implicit ev: FilterIndex[Target, Key, Next]): Traversal[Source, Next] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[Next: Eq](defaultValue: Next)(implicit evOpt: Target =:= Option[Next]): Lens[Source, Next] =
    self.adapt[Option[Next], Option[Next]].andThen(std.option.withDefault(defaultValue))

  def at[Key, Next](key: Key)(implicit evAt: At[Target, key.type, Next]): Lens[Source, Next] =
    self.andThen(evAt.at(key))

  def index[Key, Next](key: Key)(implicit evIndex: Index[Target, Key, Next]): Optional[Source, Next] =
    self.andThen(evIndex.index(key))
}

package monocle

import cats.{Applicative, Functor, Monoid}
import cats.arrow.Category
import cats.evidence.{<~<, Is}
import cats.syntax.either._
import monocle.function.Each

/** [[Iso]] is a type alias for [[PIso]] where `S` = `A` and `T` = `B`:
  * {{{
  * type Iso[S, A] = PIso[S, S, A, A]
  * }}}
  *
  * An [[Iso]] defines an isomorphism between a type S and A:
  * <pre>
  *             get
  *     -------------------->
  *   S                       A
  *     <--------------------
  *          reverseGet
  * </pre>
  *
  * A [[PIso]] allows to lift a function `f: A => B` to `S => T` and a function `g: T => S` to `B => A`
  * <pre>
  *                                                           g
  *     S           T                                   S <-------- T
  *     |           ↑                                   |           ↑
  *     |           |                                   |           |
  * get |           | reverseGet     reverse.reverseGet |           | reverse.get
  *     |           |                                   |           |
  *     ↓     f     |                                   ↓           |
  *     A --------> B                                   A           B
  * </pre>
  *
  * A [[PIso]] is also a valid [[Getter]], [[Fold]], [[PLens]], [[PPrism]], [[POptional]], [[PTraversal]] and [[PSetter]]
  *
  * @see [[monocle.law.IsoLaws]]
  *
  * @tparam S the source of a [[PIso]]
  * @tparam T the modified source of a [[PIso]]
  * @tparam A the target of a [[PIso]]
  * @tparam B the modified target of a [[PIso]]
  */
abstract class PIso[S, T, A, B] extends Serializable { self =>

  /** get the target of a [[PIso]] */
  def get(s: S): A

  /** get the modified source of a [[PIso]] */
  def reverseGet(b: B): T

  /** reverse a [[PIso]]: the source becomes the target and the target becomes the source */
  def reverse: PIso[B, A, T, S]

  /** lift a [[PIso]] to a Functor level */
  def mapping[F[_]: Functor]: PIso[F[S], F[T], F[A], F[B]] =
    PIso[F[S], F[T], F[A], F[B]](fs => Functor[F].map(fs)(self.get))(fb => Functor[F].map(fb)(self.reverseGet))

  /** find if the target satisfies the predicate */
  @inline final def find(p: A => Boolean): S => Option[A] =
    s => Some(get(s)).filter(p)

  /** check if the target satisfies the predicate */
  @inline final def exist(p: A => Boolean): S => Boolean =
    p compose get

  /** modify polymorphically the target of a [[PIso]] with a Functor function */
  @inline final def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    Functor[F].map(f(get(s)))(reverseGet)

  /** modify polymorphically the target of a [[PIso]] with a function */
  @inline final def modify(f: A => B): S => T =
    s => reverseGet(f(get(s)))

  /** set polymorphically the target of a [[PIso]] with a value */
  @inline final def set(b: B): S => T =
    _ => reverseGet(b)

  /** pair two disjoint [[PIso]] */
  @inline final def split[S1, T1, A1, B1](other: PIso[S1, T1, A1, B1]): PIso[(S, S1), (T, T1), (A, A1), (B, B1)] =
    PIso[(S, S1), (T, T1), (A, A1), (B, B1)] { case (s, s1) =>
      (get(s), other.get(s1))
    } { case (b, b1) =>
      (reverseGet(b), other.reverseGet(b1))
    }

  @inline final def first[C]: PIso[(S, C), (T, C), (A, C), (B, C)] =
    PIso[(S, C), (T, C), (A, C), (B, C)] { case (s, c) =>
      (get(s), c)
    } { case (b, c) =>
      (reverseGet(b), c)
    }

  @inline final def second[C]: PIso[(C, S), (C, T), (C, A), (C, B)] =
    PIso[(C, S), (C, T), (C, A), (C, B)] { case (c, s) =>
      (c, get(s))
    } { case (c, b) =>
      (c, reverseGet(b))
    }

  @inline final def left[C]: PIso[Either[S, C], Either[T, C], Either[A, C], Either[B, C]] =
    PIso[Either[S, C], Either[T, C], Either[A, C], Either[B, C]](_.leftMap(get))(_.leftMap(reverseGet))

  @inline final def right[C]: PIso[Either[C, S], Either[C, T], Either[C, A], Either[C, B]] =
    PIso[Either[C, S], Either[C, T], Either[C, A], Either[C, B]](_.map(get))(_.map(reverseGet))

  def each[C](implicit evTS: T =:= S, evBA: B =:= A, evEach: Each[A, C]): Traversal[S, C] =
    mono composeTraversal evEach.each

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): PPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  def mono(implicit evTS: T =:= S, evBA: B =:= A): Iso[S, A] =
    evTS.substituteCo[PIso[S, *, A, A]](evBA.substituteCo[PIso[S, T, A, *]](this))

  private def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): PIso[S, T, A1, B1] =
    evB.substituteCo[PIso[S, T, A1, *]](evA.substituteCo[PIso[S, T, *, B]](this))

  /** *******************************************************
    */
  /** Compose methods between a [[PIso]] and another Optics */
  /** *******************************************************
    */
  /** compose a [[PIso]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** Compose with a function lifted into a Getter */
  @inline def to[C](f: A => C): Getter[S, C] = composeGetter(Getter(f))

  /** compose a [[PIso]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    asGetter composeGetter other

  /** compose a [[PIso]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PIso]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[PIso]] with a [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other

  /** compose a [[PIso]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    asPrism composePrism other

  /** compose a [[PIso]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    asLens composeLens other

  /** compose a [[PIso]] with a [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    new PIso[S, T, C, D] { composeSelf =>
      def get(s: S): C =
        other.get(self.get(s))

      def reverseGet(d: D): T =
        self.reverseGet(other.reverseGet(d))

      def reverse: PIso[D, C, T, S] =
        new PIso[D, C, T, S] {
          def get(d: D): T =
            self.reverseGet(other.reverseGet(d))

          def reverseGet(s: S): C =
            other.get(self.get(s))

          def reverse: PIso[S, T, C, D] =
            composeSelf
        }
    }

  /** *****************************************
    */
  /** Experimental aliases of compose methods */
  /** *****************************************
    */
  /** alias to composeTraversal */
  @inline final def ^|->>[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline final def ^|-?[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline final def ^<-?[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline final def ^|->[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline final def ^<->[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    composeIso(other)

  /** *************************************************************
    */
  /** Transformation methods to view a [[PIso]] as another Optics */
  /** *************************************************************
    */
  /** view a [[PIso]] as a [[Fold]] */
  @inline final def asFold: Fold[S, A] =
    new Fold[S, A] {
      def foldMap[M: Monoid](f: A => M)(s: S): M =
        f(get(s))
    }

  /** view a [[PIso]] as a [[Getter]] */
  @inline final def asGetter: Getter[S, A] =
    (s: S) => self.get(s)

  /** view a [[PIso]] as a [[Setter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    new PSetter[S, T, A, B] {
      def modify(f: A => B): S => T =
        self.modify(f)

      def set(b: B): S => T =
        self.set(b)
    }

  /** view a [[PIso]] as a [[PTraversal]] */
  @inline final def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PIso]] as a [[POptional]] */
  @inline final def asOptional: POptional[S, T, A, B] =
    new POptional[S, T, A, B] {
      def getOrModify(s: S): Either[T, A] =
        Either.right(get(s))

      def set(b: B): S => T =
        self.set(b)

      def getOption(s: S): Option[A] =
        Some(self.get(s))

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PIso]] as a [[PPrism]] */
  @inline final def asPrism: PPrism[S, T, A, B] =
    new PPrism[S, T, A, B] {
      def getOrModify(s: S): Either[T, A] =
        Either.right(get(s))

      def reverseGet(b: B): T =
        self.reverseGet(b)

      def getOption(s: S): Option[A] =
        Some(self.get(s))
    }

  /** view a [[PIso]] as a [[PLens]] */
  @inline final def asLens: PLens[S, T, A, B] =
    new PLens[S, T, A, B] {
      def get(s: S): A =
        self.get(s)

      def set(b: B): S => T =
        self.set(b)

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** **********************************************************************
    */
  /** Apply methods to treat a [[PIso]] as smart constructors for type T */
  /** **********************************************************************
    */
  def apply()(implicit ev: Is[B, Unit]): T =
    ev.substitute[PIso[S, T, A, *]](self).reverseGet(())

  def apply(b: B): T = reverseGet(b)

  def apply[C, D](c: C, d: D)(implicit ev: (C, D) <~< B): T = apply(ev((c, d)))

  def apply[C, D, E](c: C, d: D, e: E)(implicit ev: (C, D, E) <~< B): T =
    apply(ev((c, d, e)))

  def apply[C, D, E, F](c: C, d: D, e: E, f: F)(implicit ev: (C, D, E, F) <~< B): T =
    apply(ev((c, d, e, f)))

  def apply[C, D, E, F, G](c: C, d: D, e: E, f: F, g: G)(implicit ev: (C, D, E, F, G) <~< B): T =
    apply(ev((c, d, e, f, g)))

  def apply[C, D, E, F, G, H](c: C, d: D, e: E, f: F, g: G, h: H)(implicit ev: (C, D, E, F, G, H) <~< B): T =
    apply(ev((c, d, e, f, g, h)))

  def unapply(obj: S): Some[A] = Some(get(obj))
}

object PIso extends IsoInstances {

  /** create a [[PIso]] using a pair of functions: one to get the target and one to get the source. */
  def apply[S, T, A, B](_get: S => A)(_reverseGet: B => T): PIso[S, T, A, B] =
    new PIso[S, T, A, B] { self =>
      def get(s: S): A =
        _get(s)

      def reverseGet(b: B): T =
        _reverseGet(b)

      def reverse: PIso[B, A, T, S] =
        new PIso[B, A, T, S] {
          def get(b: B): T =
            _reverseGet(b)

          def reverseGet(s: S): A =
            _get(s)

          def reverse: PIso[S, T, A, B] =
            self
        }
    }

  /** create a [[PIso]] between any type and itself. id is the zero element of optics composition,
    * for all optics o of type O (e.g. Lens, Iso, Prism, ...):
    * o      composeIso Iso.id == o
    * Iso.id composeO   o        == o (replace composeO by composeLens, composeIso, composePrism, ...)
    */
  def id[S, T]: PIso[S, T, S, T] =
    new PIso[S, T, S, T] { self =>
      def get(s: S): S        = s
      def reverseGet(t: T): T = t
      def reverse: PIso[T, S, T, S] =
        new PIso[T, S, T, S] {
          def get(t: T): T              = t
          def reverseGet(s: S): S       = s
          def reverse: PIso[S, T, S, T] = self
        }
    }
}

object Iso {

  /** alias for [[PIso]] apply when S = T and A = B */
  def apply[S, A](get: S => A)(reverseGet: A => S): Iso[S, A] =
    PIso(get)(reverseGet)

  /** alias for [[PIso]] id when S = T and A = B */
  def id[S]: Iso[S, S] =
    PIso.id[S, S]

  /** create an [[Iso]] from a function that is its own inverse */
  def involuted[A](update: A => A): Iso[A, A] =
    Iso(update)(update)
}

sealed abstract class IsoInstances {
  implicit val isoCategory: Category[Iso] = new Category[Iso] {
    def id[A]: Iso[A, A] =
      Iso.id[A]

    def compose[A, B, C](f: Iso[B, C], g: Iso[A, B]): Iso[A, C] =
      g composeIso f
  }
}

package monocle

import scalaz.Isomorphism.{<=>, <~>}
import scalaz.{Applicative, Functor, Maybe, Monoid, \/}

/**
 * A [[PIso]] defines an isomorphism between types S, A and B, T:
 * <pre>
 *              get                           reverse.get
 *     -------------------->             -------------------->
 *   S                       A         T                       B
 *     <--------------------             <--------------------
 *       reverse.reverseGet                   reverseGet
 * </pre>
 *
 * In addition, if f and g forms an isomorphism between `A` and `B`, i.e. if `f . g = id` and `g . f = id`,
 * then a [[PIso]] defines an isomorphism between `S` and `T`:
 * <pre>
 *     S           T                                   S           T
 *     |           ↑                                   ↑           |
 *     |           |                                   |           |
 * get |           | reverseGet     reverse.reverseGet |           | reverse.get
 *     |           |                                   |           |
 *     ↓     f     |                                   |     g     ↓
 *     A --------> B                                   A <-------- B
 * </pre>
 *
 * [[Iso]] is a type alias for [[PIso]] where `S` = `A` and `T` = `B`:
 * {{{
 * type Iso[S, A] = PIso[S, S, A, A]
 * }}}
 *
 * A [[PIso]] is also a valid [[Getter]], [[Fold]], [[PLens]], [[PPrism]], [[POptional]], [[PTraversal]] and [[PSetter]]
 *
 * @see IsoLaws in monocle-law module
 *
 * @tparam S the source of a [[PIso]]
 * @tparam T the modified source of a [[PIso]]
 * @tparam A the target of a [[PIso]]
 * @tparam B the modified target of a [[PIso]]
 */
abstract class PIso[S, T, A, B] private[monocle]{ self =>

  /** get the target of a [[PIso]] */
  def get(s: S): A

  /** get the modified source of a [[PIso]] */
  def reverseGet(b: B): T

  /** modify polymorphically the target of a [[PIso]] with a [[Functor]] function */
  def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T]

  /** modify polymorphically the target of a [[PIso]] with a function */
  def modify(f: A => B): S => T

  /** reverse a [[PIso]]: the source becomes the target and the target becomes the source */
  def reverse: PIso[B, A, T, S]

  /** set polymorphically the target of a [[PIso]] with a value */
  @inline final def set(b: B): S => T =
    _ => reverseGet(b)

  /**********************************************************/
  /** Compose methods between a [[PIso]] and another Optics */
  /**********************************************************/

  /** compose a [[PIso]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

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
    new PIso[S, T, C, D]{ composeSelf =>
      def get(s: S): C =
        other.get(self.get(s))

      def reverseGet(d: D): T =
        self.reverseGet(other.reverseGet(d))

      def modifyF[F[_]: Functor](f: C => F[D])(s: S): F[T] =
        self.modifyF(other.modifyF(f))(s)

      def modify(f: C => D): S => T =
        self.modify(other.modify(f))

      val reverse: PIso[D, C, T, S] =
        new PIso[D, C, T, S]{
          def get(d: D): T =
            self.reverseGet(other.reverseGet(d))

          def reverseGet(s: S): C =
            other.get(self.get(s))

          def modifyF[F[_] : Functor](f: (T) => F[S])(s: D): F[C] =
            other.reverse.modifyF(self.reverse.modifyF(f))(s)

          def modify(f: T => S): D => C =
            other.reverse.modify(self.reverse.modify(f))

          def reverse: PIso[S, T, C, D] =
            composeSelf
        }
    }

  /********************************************/
  /** Experimental aliases of compose methods */
  /********************************************/

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

  /****************************************************************/
  /** Transformation methods to view a [[PIso]] as another Optics */
  /****************************************************************/

  /** view a [[PIso]] as a [[Fold]] */
  @inline final def asFold: Fold[S, A] =
    new Fold[S, A]{
      def foldMap[M: Monoid](f: A => M)(s: S): M =
        f(get(s))
    }

  /** view a [[PIso]] as a [[Getter]] */
  @inline final def asGetter: Getter[S, A] =
    new Getter(get)

  /** view a [[PIso]] as a [[Setter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    new PSetter(modify)

  /** view a [[PIso]] as a [[PTraversal]] */
  @inline final def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PIso]] as a [[POptional]] */
  @inline final def asOptional: POptional[S, T, A, B] =
    new POptional(\/.right compose get, set){
      def getMaybe(s: S): Maybe[A] =
        Maybe.just(self.get(s))

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PIso]] as a [[PPrism]] */
  @inline final def asPrism: PPrism[S, T, A, B] =
    new PPrism(\/.right compose get, reverseGet){
      def getMaybe(s: S): Maybe[A] =
        Maybe.just(self.get(s))

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PIso]] as a [[PLens]] */
  @inline final def asLens: PLens[S, T, A, B] =
    new PLens[S, T, A, B]{
      def get(s: S): A =
        self.get(s)

      def set(b: B): S => T =
        self.set(b)

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

}

object PIso {
  /** create a [[PIso]] using a pair of functions: one to get the target and one to get the source. */
  def apply[S, T, A, B](_get: S => A)(_reverseGet: B => T): PIso[S, T, A, B] =
    new PIso[S, T, A, B]{ self =>
      def get(s: S): A =
        _get(s)

      def reverseGet(b: B): T =
        _reverseGet(b)

      def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
        Functor[F].map(f(_get(s)))(_reverseGet)

      def modify(f: A => B): S => T =
        s => _reverseGet(f(_get(s)))

      val reverse: PIso[B, A, T, S] =
        new PIso[B, A, T, S] {
          def get(b: B): T =
            _reverseGet(b)

          def reverseGet(s: S): A =
            _get(s)

          def modifyF[F[_] : Functor](f: T => F[S])(s: B): F[A] =
            Functor[F].map(f(_reverseGet(s)))(_get)

          def modify(f: T => S): B => A =
            b => _get(f(_reverseGet(b)))

          def reverse: PIso[S, T, A, B] =
            self
        }
    }

  /**
   * create a [[PIso]] between any type and itself. id is the zero element of optics composition,
   * for all optics o of type O (e.g. Lens, Iso, Prism, ...):
   * o      composeIso Iso.id == o
   * Iso.id composeO   o        == o (replace composeO by composeLens, composeIso, composePrism, ...)
   */
  def id[S, T]: PIso[S, T, S, T] =
    new PIso[S, T, S, T] { self =>
      def get(s: S): S = s
      def reverseGet(t: T): T = t
      def modifyF[F[_]: Functor](f: S => F[T])(s: S): F[T] = f(s)
      def modify(f: S => T): S => T = f
      val reverse: PIso[T, S, T, S] =
        new PIso[T, S, T, S] {
          def get(t: T): T = t
          def reverseGet(s: S): S = s
          def modify(f: T => S): T => S = f
          def modifyF[F[_]: Functor](f: T => F[S])(t: T): F[S] = f(t)
          def reverse: PIso[S, T, S, T] = self
        }
    }

  /** transform an [[scalaz.Isomorphisms.Iso2]] in a [[PIso]] */
  def fromIsoFunctor[F[_], G[_], A, B](isoFunctor: F <~> G): PIso[F[A], F[B], G[A], G[B]] =
    PIso(isoFunctor.to.apply[A])(isoFunctor.from.apply[B])
}

object Iso {
  /** alias for [[PIso]] apply when S = T and A = B */
  def apply[S, A](get: S => A)(reverseGet: A => S): Iso[S, A] =
    PIso(get)(reverseGet)

  /** alias for [[PIso]] id when S = T and A = B */
  def id[S]: Iso[S, S] =
    Iso(identity[S])(identity[S])

  /** transform an [[scalaz.Isomorphisms.Iso]] in a [[Iso]] */
  def fromIsoSet[A, B](isoSet: A <=> B): Iso[A, B] =
    Iso(isoSet.to)(isoSet.from)
}

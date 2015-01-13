package monocle

import scalaz.{\/, Applicative, Maybe, Monoid, Functor}

/**
 * A [[PLens]] can be seen as a pair of functions:
 *  - `get: S      => A` i.e. from an `S`, we can extract an `A`
 *  - `set: (B, S) => T` i.e. if we replace an `A` by a `B` in an `S`, we obtain a `T`
 *
 * A [[PLens]] could also be defined as a weaker [[PIso]] where set requires
 * an additional parameter than reverseGet.
 *
 * [[PLens]] stands for Polymorphic Lens as it set and modify methods change
 * a type `A` to `B` and `S` to `T`.
 * [[Lens]] is a type alias for [[PLens]] restricted to monomoprhic updates:
 * {{{
 * type Lens[S, A] = PLens[S, S, A, A]
 * }}}
 *
 * A [[PLens]] is also a valid [[Getter]], [[Fold]], [[POptional]],
 * [[PTraversal]] and [[PSetter]]
 *
 * Typically a [[PLens]] or [[Lens]] can be defined between a [[Product]]
 * (e.g. case class, tuple, HList) and one of it is component.
 *
 * @see LensLaws in monocle-law module
 *
 * @tparam S the source of a [[PLens]]
 * @tparam T the modified source of a [[PLens]]
 * @tparam A the target of a [[PLens]]
 * @tparam B the modified target of a [[PLens]]
 *
 * @param get get the target of a [[PLens]]
 * @param set set polymorphically the target of a [[PLens]] with a value
 */
abstract class PLens[S, T, A, B] private[monocle](val get: S => A, val set: B => S => T){ self =>

  /** modify polymorphically the target of a [[PLens]] using [[Functor]] function */
  def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T]

  /** modify polymorphically the target of a [[PLens]] using a function */
  def modify(f: A => B): S => T

  /***********************************************************/
  /** Compose methods between a [[PLens]] and another Optics */
  /***********************************************************/

  /** compose a [[PLens]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[PLens]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    asGetter composeGetter other

  /** compose a [[PLens]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PLens]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[PLens]] with an [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other

  /** compose a [[PLens]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other.asOptional

  /** compose a [[PLens]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    new PLens[S, T, C, D](other.get compose self.get, c => modify(other.set(c))){
      def modifyF[F[_] : Functor](f: C => F[D])(s: S): F[T] =
        self.modifyF(other.modifyF(f))(s)

      def modify(f: C => D): S => T =
        self.modify(other.modify(f))
    }

  /** compose a [[PLens]] with an [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    composeLens(other.asLens)

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
  @inline final def ^<-?[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline final def ^|->[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline final def ^<->[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    composeIso(other)

  /************************************************************************************************/
  /** Transformation methods to view a [[PLens]] as another Optics                                */
  /************************************************************************************************/

  /** view a [[PLens]] as a [[Fold]] */
  @inline final def asFold: Fold[S, A] =
    new Fold[S, A] {
      def foldMap[M: Monoid](f: A => M)(s: S): M =
        f(get(s))
    }

  /** view a [[PLens]] as a [[Getter]] */
  @inline final def asGetter: Getter[S, A] =
    new Getter(get)

  /** view a [[PLens]] as a [[PSetter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    new PSetter(modify)

  /** view a [[PLens]] as a [[PTraversal]] */
  @inline final def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PLens]] as an [[POptional]] */
  @inline final def asOptional: POptional[S, T, A, B] =
    new POptional(\/.right compose get, set){
      def getMaybe(s: S): Maybe[A] =
        Maybe.just(self.get(s))

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_] : Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

}

object PLens {
  /**
   * create a [[PLens]] using a pair of functions: one to get the target, one to set the target.
   * @see macro module for methods generating [[PLens]] with less boiler plate
   */
  def apply[S, T, A, B](get: S => A)(set: B => S => T): PLens[S, T, A, B] =
    new PLens(get, set){
      def modifyF[F[_] : Functor](f: A => F[B])(s: S): F[T] =
        Functor[F].map(f(get(s)))(set(_)(s))

      def modify(f: A => B): S => T =
       s => set(f(get(s)))(s)
    }

}

object Lens {
  /** alias for [[PLens]] apply with a monomorphic set function */
  def apply[S, A](get: S => A)(set: A => S => S): Lens[S, A] =
    PLens(get)(set)
}

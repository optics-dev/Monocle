package monocle

import scalaz.{\/, Applicative, Monoid, Functor}

/**
 * A [[PLens]] can be seen as a pair of functions:
 *  get: S      => A  i.e. from an S, we can extract an A
 *  set: (B, S) => T  i.e. if we replace an A by a B in a S, we obtain a T
 *
 * A [[PLens]] could also be defined as a weaker [[PIso]] where set requires
 * an additional parameter than reverseGet.
 *
 * [[PLens]] stands for Polymorphic Lens as it set and modify methods change
 * a type A to B and S to T.
 * [[Lens]] is a type alias for [[PLens]] restricted to monomoprhic updates:
 *
 * type Lens[S, A] = PLens[S, S, A, A]
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
final class PLens[S, T, A, B](val get: S => A, val set: B => S => T){ self =>

  /** modify polymorphically the target of a [[PLens]] using [[Functor]] function */
  @inline def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    Functor[F].map(f(get(s)))(set(_)(s))

  /** modify polymorphically the target of a [[PLens]] using a function */
  @inline def modify(f: A => B): S => T =
    s => set(f(get(s)))(s)

  /***********************************************************/
  /** Compose methods between a [[PLens]] and another Optics */
  /***********************************************************/

  /** compose a [[PLens]] with a [[Fold]] */
  @inline def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[PLens]] with a [[Getter]] */
  @inline def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    asGetter composeGetter other

  /** compose a [[PLens]] with a [[PSetter]] */
  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PLens]] with a [[PTraversal]] */
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[PLens]] with an [[POptional]] */
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other

  /** compose a [[PLens]] with a [[PPrism]] */
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other.asOptional

  /** compose a [[PLens]] with a [[PLens]] */
  def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    PLens[S, T, C, D](other.get compose get)(c => modify(other.set(c)))

  /** compose a [[PLens]] with an [[PIso]] */
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    composeLens(other.asLens)

  /************************************************************************************************/
  /** Transformation methods to view a [[PLens]] as another Optics                                */
  /************************************************************************************************/

  /** view a [[PLens]] as a [[Fold]] */
  def asFold: Fold[S, A] = new Fold[S, A] {
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M =
      f(get(s))
  }

  /** view a [[PLens]] as a [[Getter]] */
  @inline def asGetter: Getter[S, A] =
    Getter[S, A](get)

  /** view a [[PLens]] as a [[PSetter]] */
  @inline def asSetter: PSetter[S, T, A, B] =
    PSetter[S, T, A, B](modify)

  /** view a [[PLens]] as a [[PTraversal]] */
  def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      @inline def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PLens]] as an [[POptional]] */
  @inline def asOptional: POptional[S, T, A, B] =
    POptional[S, T, A, B](\/.right compose get)(set)

}

object PLens {
  /**
   * create a [[PLens]] using a pair of functions: one to get the target, one to set the target.
   * @see macro module for methods generating [[PLens]] with less boiler plate
   */
  def apply[S, T, A, B](get: S => A)(set: B => S => T): PLens[S, T, A, B] =
    new PLens(get, set)

}

object Lens {
  /** alias for [[PLens]] apply with a monomorphic set function */
  def apply[S, A](get: S => A)(set: A => S => S): Lens[S, A] =
    new PLens(get, set)
}
package monocle

import scalaz.{Applicative, Maybe, Monoid, \/}

/**
 * A [[POptional]] can be seen as a pair of functions:
 *  getOrModify: S      => T \/ A
 *  set        : (B, S) => T
 *
 * A [[POptional]] could also be defined as a weaker [[PLens]] and
 * weaker [[PPrism]]
 *
 * [[POptional]] stands for Polymorphic Optional as it set and modify methods change
 * a type A to B and S to T.
 * [[Optional]] is a type alias for [[POptional]] restricted to monomoprhic updates:
 *
 * type Optional[S, A] = POptional[S, S, A, A]
 *
 * @see OptionalLaws in monocle-law module
 *
 * @tparam S the source of a [[POptional]]
 * @tparam T the modified source of a [[POptional]]
 * @tparam A the target of a [[POptional]]
 * @tparam B the modified target of a [[POptional]]
 *
 * @param getOrModify get the target of a [[POptional]] or modify the source in case there is no target
 * @param set set polymorphically the target of a [[POptional]] with a value
 */
final class POptional[S, T, A, B](val getOrModify: S => T \/ A, val set: B => S => T) { self =>


  /** get the target of a [[PPrism]] or nothing if there is no target */
  @inline def getMaybe(s: S): Maybe[A] =
    getOrModify(s).toMaybe


  /** modify polymorphically the target of a [[POptional]] with an [[Applicative]] function */
  @inline def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    getOrModify(s).fold(
      t => Applicative[F].point(t),
      a => Applicative[F].map(f(a))(set(_)(s))
    )

  /** modify polymorphically the target of a [[POptional]] with a function */
  @inline def modify(f: A => B): S => T =
    s => getOrModify(s).fold(identity, a => set(f(a))(s))

  /**
   * modify polymorphically the target of a [[POptional]] with a function.
   * return empty if the [[POptional]] is not matching
   */
  @inline def modifyMaybe(f: A => B): S => Maybe[T] =
    s => getMaybe(s).map(_ => modify(f)(s))


  /**
   * set polymorphically the target of a [[POptional]] with a value.
   * return empty if the [[POptional]] is not matching
   */
  @inline def setMaybe(b: B): S => Maybe[T] =
    modifyMaybe(_ => b)

  /** check if a [[POptional]] has a target */
  @inline def isMatching(s: S): Boolean =
    getMaybe(s).isJust

  /***************************************************************/
  /** Compose methods between a [[POptional]] and another Optics */
  /***************************************************************/

  /** compose a [[POptional]] with a [[Fold]] */
  @inline def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[POptional]] with a [[Getter]] */
  @inline def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    asFold composeGetter other

  /** compose a [[POptional]] with a [[PSetter]] */
  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[POptional]] with a [[PTraversal]] */
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[POptional]] with a [[POptional]] */
  def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    new POptional(
      s      => getOrModify(s).flatMap(a => other.getOrModify(a).bimap(set(_)(s), identity)),
      d => s => modify(other.set(d))(s)
    )

  /** compose a [[POptional]] with a [[PPrism]] */
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /** compose a [[POptional]] with a [[PLens]] */
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /** compose a [[POptional]] with a [[PIso]] */
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /*********************************************************************/
  /** Transformation methods to view a [[POptional]] as another Optics */
  /*********************************************************************/

  /** view a [[POptional]] as a [[Fold]] */
  def asFold: Fold[S, A] = new Fold[S, A]{
    @inline def foldMap[M: Monoid](f: A => M)(s: S): M =
      self.getMaybe(s) map f getOrElse Monoid[M].zero
  }

  /** view a [[POptional]] as a [[PSetter]] */
  @inline def asSetter: PSetter[S, T, A, B] =
    PSetter[S, T, A, B](modify)

  /** view a [[POptional]] as a [[PTraversal]] */
  def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    @inline def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      self.modifyF(f)(s)
  }

}

object POptional {
  def apply[S, T, A, B](_getOrModify: S => T \/ A)(_set: B => S => T): POptional[S, T, A, B] =
    new POptional(_getOrModify, _set)
}

object Optional {
  def apply[S, A](_getMaybe: S => Maybe[A])(_set: A => S => S): Optional[S, A] =
    new POptional[S, S, A, A](s => _getMaybe(s) \/> s, _set)
}

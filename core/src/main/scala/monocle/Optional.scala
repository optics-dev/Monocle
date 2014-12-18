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
abstract class POptional[S, T, A, B] private[monocle](val getOrModify: S => T \/ A, val set: B => S => T) { self =>

  /** get the target of a [[PPrism]] or nothing if there is no target */
  def getMaybe(s: S): Maybe[A]

  /** modify polymorphically the target of a [[POptional]] with an [[Applicative]] function */
  def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T]

  /** modify polymorphically the target of a [[POptional]] with a function */
  def modify(f: A => B): S => T

  /**
   * modify polymorphically the target of a [[POptional]] with a function.
   * return empty if the [[POptional]] is not matching
   */
  @inline final def modifyMaybe(f: A => B): S => Maybe[T] =
    s => getMaybe(s).map(_ => modify(f)(s))

  /**
   * set polymorphically the target of a [[POptional]] with a value.
   * return empty if the [[POptional]] is not matching
   */
  @inline final def setMaybe(b: B): S => Maybe[T] =
    modifyMaybe(_ => b)

  /** check if a [[POptional]] has a target */
  @inline final def isMatching(s: S): Boolean =
    getMaybe(s).isJust

  /***************************************************************/
  /** Compose methods between a [[POptional]] and another Optics */
  /***************************************************************/

  /** compose a [[POptional]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[POptional]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    asFold composeGetter other

  /** compose a [[POptional]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[POptional]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[POptional]] with a [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    new POptional[S, T, C, D](
      s      => getOrModify(s).flatMap(a => other.getOrModify(a).bimap(set(_)(s), identity)),
      d => s => modify(other.set(d))(s)
    ){
      def getMaybe(s: S): Maybe[C] =
        self.getMaybe(s) flatMap other.getMaybe

      def modifyF[F[_] : Applicative](f: C => F[D])(s: S): F[T] =
        self.modifyF(other.modifyF(f))(s)

      def modify(f: C => D): S => T =
        self.modify(other.modify(f))
    }

  /** compose a [[POptional]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /** compose a [[POptional]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

  /** compose a [[POptional]] with a [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): POptional[S, T, C, D] =
    composeOptional(other.asOptional)

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
  @inline final def ^|->[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline final def ^<->[C, D](other: PIso[A, B, C, D]): POptional[S, T, C, D] =
    composeIso(other)

  /*********************************************************************/
  /** Transformation methods to view a [[POptional]] as another Optics */
  /*********************************************************************/

  /** view a [[POptional]] as a [[Fold]] */
  @inline final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M =
      self.getMaybe(s) map f getOrElse Monoid[M].zero
  }

  /** view a [[POptional]] as a [[PSetter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    new PSetter(modify)

  /** view a [[POptional]] as a [[PTraversal]] */
  @inline final def asTraversal: PTraversal[S, T, A, B] = new PTraversal[S, T, A, B] {
    def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
      self.modifyF(f)(s)
  }

}

object POptional {
  /** create a [[POptional]] using the canonical functions: getOrModify and set */
  def apply[S, T, A, B](_getOrModify: S => T \/ A)(_set: B => S => T): POptional[S, T, A, B] =
    new POptional(_getOrModify, _set){
      def getMaybe(s: S): Maybe[A] =
        getOrModify(s).toMaybe

      def modifyF[F[_] : Applicative](f: A => F[B])(s: S): F[T] =
        getOrModify(s).fold(
          t => Applicative[F].point(t),
          a => Applicative[F].map(f(a))(set(_)(s))
        )

      def modify(f: A => B): S => T =
        s => getOrModify(s).fold(identity, a => set(f(a))(s))
    }
}

object Optional {
  /** alias for [[POptional]] apply restricted to monomorphic update */
  def apply[S, A](_getMaybe: S => Maybe[A])(_set: A => S => S): Optional[S, A] =
    new POptional[S, S, A, A](s => _getMaybe(s) \/> s, _set){
      def getMaybe(s: S): Maybe[A] =
        _getMaybe(s)

      def modifyF[F[_] : Applicative](f: A => F[A])(s: S): F[S] =
        getOrModify(s).fold(
          t => Applicative[F].point(t),
          a => Applicative[F].map(f(a))(set(_)(s))
        )

      def modify(f: A => A): S => S =
        s => getOrModify(s).fold(identity, a => set(f(a))(s))
    }
}

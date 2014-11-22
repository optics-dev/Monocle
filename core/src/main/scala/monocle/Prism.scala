package monocle

import scalaz.{Applicative, Maybe, Monoid, \/}


/**
 * A [[PPrism]] can be seen as a pair of functions:
 *  getOrModify: S => T \/ A
 *  reverseGet : B => T
 *
 * A [[PPrism]] could also be defined as a weaker [[PIso]] where get can fail.
 *
 * Typically a [[PPrism]] or [[Prism]] encodes the relation between a Sum or
 * CoProduct type (e.g. sealed trait) and one of it is element.
 *
 * [[PPrism]] stands for Polymorphic Prism as it set and modify methods change
 * a type A to B and S to T.
 * [[Prism]] is a type alias for [[PPrism]] where the type of target cannot be modified:
 *
 * type Prism[S, A] = PPrism[S, S, A, A]
 *
 * A [[PPrism]] is also a valid  [[Fold]], [[POptional]], [[PTraversal]] and [[PSetter]]
 *
 * @see PrismLaws in monocle-law module
 *
 * @tparam S the source of a [[PPrism]]
 * @tparam T the modified source of a [[PPrism]]
 * @tparam A the target of a [[PPrism]]
 * @tparam B the modified target of a [[PPrism]]
 *
 * @param getOrModify get the target of a [[PPrism]] or modify the source in case there is no target
 * @param reverseGet get the modified source of a [[PIso]]
 */
final class PPrism[S, T, A, B](val getOrModify: S => T \/ A, val reverseGet: B => T){ self =>

  /** get the target of a [[PPrism]] or nothing if there is no target */
  @inline def getMaybe(s: S): Maybe[A] =
    getOrModify(s).toMaybe

  /** create a [[Getter]] from the modified target to the modified source of a [[PPrism]] */
  @inline def re: Getter[B, T] =
    Getter(reverseGet)

  /** modify polymorphically the target of a [[PPrism]] with an [[Applicative]] function */
  @inline def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    getOrModify(s).fold(
      t => Applicative[F].point(t),
      a => Applicative[F].map(f(a))(reverseGet)
    )

  /** modify polymorphically the target of a [[PPrism]] with a function */
  @inline def modify(f: A => B): S => T =
    getOrModify(_).fold(identity, reverseGet compose f)

  /**
   * modify polymorphically the target of a [[PPrism]] with a function.
   * return empty if the [[PPrism]] is not matching
   */
  @inline def modifyMaybe(f: A => B): S => Maybe[T] =
    s => getMaybe(s).map(_ => modify(f)(s))

  /** set polymorphically the target of a [[PPrism]] with a value */
  @inline def set(b: B): S => T =
    modify(_ => b)

  /**
   * set polymorphically the target of a [[PPrism]] with a value.
   * return empty if the [[PPrism]] is not matching
   */
  @inline def setMaybe(b: B): S => Maybe[T] =
    modifyMaybe(_ => b)

  /** check if a [[PPrism]] has a target */
  @inline def isMatching(s: S): Boolean =
    getMaybe(s).isJust

  /************************************************************/
  /** Compose methods between a [[PPrism]] and another Optics */
  /************************************************************/

  /** compose a [[PPrism]] with a [[Fold]] */
  @inline def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[PPrism]] with a [[Getter]] */
  @inline def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    asFold composeGetter other

  /** compose a [[PPrism]] with a [[PSetter]] */
  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PPrism]] with a [[PTraversal]] */
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[PPrism]] with a [[POptional]] */
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other

  /** compose a [[PPrism]] with a [[PLens]] */
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other.asOptional

  /** compose a [[PPrism]] with a [[PPrism]] */
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    new PPrism[S, T, C, D](
      s => getOrModify(s).flatMap(a => other.getOrModify(a).bimap(set(_)(s), identity)),
      reverseGet compose other.reverseGet
    )

  /** compose a [[PPrism]] with a [[PIso]] */
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): PPrism[S, T, C, D] =
    composePrism(other.asPrism)

  /******************************************************************/
  /** Transformation methods to view a [[PPrism]] as another Optics */
  /******************************************************************/

  /** view a [[PPrism]] as a [[Fold]] */
  @inline def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M =
      getMaybe(s) map f getOrElse Monoid[M].zero
  }

  /** view a [[PPrism]] as a [[Setter]] */
  @inline def asSetter: PSetter[S, T, A, B] =
    PSetter(modify)

  /** view a [[PPrism]] as a [[PTraversal]] */
  @inline def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PPrism]] as a [[POptional]] */
  @inline def asOptional: POptional[S, T, A, B] =
    new POptional(getOrModify, set)

}

object PPrism {
  /** create a [[PPrism]] using the canonical functions: getOrModify and reverseGet */
  def apply[S, T, A, B](getOrModify: S => T \/ A)(reverseGet: B => T): PPrism[S, T, A, B] =
    new PPrism(getOrModify, reverseGet)

}

object Prism {
  /** alias for [[PPrism]] apply restricted to monomorphic update */
  def apply[S, A](getMaybe: S => Maybe[A])(reverseGet: A => S): Prism[S, A] =
    new PPrism[S, S, A, A](s => getMaybe(s) \/> s, reverseGet)
}

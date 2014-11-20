package monocle

import scalaz.Functor

/**
 * A [[PSetter]] is a generalisation of [[Functor]] map:
 *   map:    (A => B) => F[A] => F[B]
 *   modify: (A => B) => S    => T
 *
 * [[PSetter]] stands for Polymorphic Setter as it set and modify methods change
 * a type A to B and S to T.
 * [[Setter]] is a type alias for [[PSetter]] restricted to monomoprhic updates:
 *
 * type Setter[S, A] = PSetter[S, S, A, A]
 *
 * [[PTraversal]], [[POptional]], [[PPrism]], [[PLens]] and [[PIso]] are valid [[PSetter]]
 *
 * @see SetterLaws in monocle-law module
 *
 * @tparam S the source of the [[PSetter]]
 * @tparam T the modified source of the [[PSetter]]
 * @tparam A the target of the [[PSetter]]
 * @tparam B the modified target of the [[PSetter]]
 */
final case class PSetter[S, T, A, B](modify: (A => B) => S => T) {

  /** set polymorphically the target of a [[PSetter]] with a value */
  @inline def set(b: B): S => T =
    modify(_ => b)

  /*************************************************************/
  /** Compose methods between a [[PSetter]] and another Optics */
  /*************************************************************/

  /** compose a [[PSetter]] with a [[PSetter]] */
  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    PSetter[S, T, C, D](modify compose other.modify)

  /** compose a [[PSetter]] with a [[PTraversal]] */
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[POptional]] */
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[PPrism]] */
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[PLens]] */
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[PIso]] */
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)
}

object PSetter {
  /** create a [[PSetter]] from a [[Functor]] */
  def fromFunctor[F[_]: Functor, A, B]: PSetter[F[A], F[B], A, B] =
    PSetter[F[A], F[B], A, B](f => Functor[F].map(_)(f))
}

object Setter {
  /** alias for [[PSetter]] apply with a monomorphic modify function*/
  @inline def apply[S, A](modify: (A => A) => S => S): Setter[S, A] =
    PSetter(modify)
}
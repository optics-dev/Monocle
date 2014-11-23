package monocle

import scalaz.Isomorphism.{<=>, <~>}
import scalaz.{Applicative, Functor, Monoid, \/}

/**
 * A [[PIso]] defines an isomorphism between types S, A and B, T:
 *
 *              get                           reverse.get
 *     -------------------->             -------------------->
 *   S                       A         T                       B
 *     <--------------------             <--------------------
 *       reverse.reverseGet                   reverseGet
 *
 *
 * In addition, if f and g forms an isomorphism between A and B, i.e. if f . g = id and g . f = id,
 * then a [[PIso]] defines an isomorphism between S and T:
 *
 *
 *     S           T                                   S           T
 *     |           ↑                                   ↑           |
 *     |           |                                   |           |
 * get |           | reverseGet     reverse.reverseGet |           | reverse.get
 *     |           |                                   |           |
 *     ↓     f     |                                   |     g     ↓
 *     A --------> B                                   A <-------- B
 *
 * [[Iso]] is a type alias for [[PIso]] where S = A and T = B:
 *
 * type Iso[S, A] = PIso[S, S, A, A]
 *
 * A [[PIso]] is also a valid [[Getter]], [[Fold]], [[PLens]], [[PPrism]], [[POptional]], [[PTraversal]] and [[PSetter]]
 *
 * @see IsoLaws in monocle-law module
 *
 * @tparam S the source of a [[PIso]]
 * @tparam T the modified source of a [[PIso]]
 * @tparam A the target of a [[PIso]]
 * @tparam B the modified target of a [[PIso]]
 *
 * @param get get the target of a [[PIso]]
 * @param reverseGet get the modified source of a [[PIso]]
 */
final class PIso[S, T, A, B] private[monocle](val get: S => A, val reverseGet: B => T){ self =>

  /** reverse a [[PIso]]: the source becomes the target and the target becomes the source */
  @inline def reverse: PIso[B, A, T, S] =
    new PIso(reverseGet, get)

  /** modify polymorphically the target of a [[PIso]] with a [[Functor]] function */
  @inline def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
    Functor[F].map(f(get(s)))(reverseGet)

  /** modify polymorphically the target of a [[PIso]] with a function */
  @inline def modify(f: A => B): S => T =
    reverseGet compose f compose get

  /** set polymorphically the target of a [[PIso]] with a value */
  @inline def set(b: B): S => T =
    _ => reverseGet(b)

  /**********************************************************/
  /** Compose methods between a [[PIso]] and another Optics */
  /**********************************************************/

  /** compose a [[PIso]] with a [[Fold]] */
  @inline def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[PIso]] with a [[Getter]] */
  @inline def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    asGetter composeGetter other

  /** compose a [[PIso]] with a [[PSetter]] */
  @inline def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PIso]] with a [[PTraversal]] */
  @inline def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal composeTraversal other

  /** compose a [[PIso]] with a [[POptional]] */
  @inline def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional composeOptional other

  /** compose a [[PIso]] with a [[PPrism]] */
  @inline def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    asPrism composePrism other

  /** compose a [[PIso]] with a [[PLens]] */
  @inline def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    asLens composeLens other

  /** compose a [[PIso]] with a [[PIso]] */
  @inline def composeIso[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    new PIso(other.get compose get, reverseGet compose other.reverseGet)

  /****************************************************************/
  /** Transformation methods to view a [[PIso]] as another Optics */
  /****************************************************************/

  /** view a [[PIso]] as a [[Fold]] */
  @inline def asFold: Fold[S, A] =
    new Fold[S, A]{
      def foldMap[M: Monoid](f: A => M)(s: S): M =
        f(get(s))
    }

  /** view a [[PIso]] as a [[Getter]] */
  @inline def asGetter: Getter[S, A] =
    Getter(get)

  /** view a [[PIso]] as a [[Setter]] */
  @inline def asSetter: PSetter[S, T, A, B] =
    new PSetter(modify)

  /** view a [[PIso]] as a [[PTraversal]] */
  @inline def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PIso]] as a [[POptional]] */
  @inline def asOptional: POptional[S, T, A, B] =
    new POptional(\/.right compose get, set)

  /** view a [[PIso]] as a [[PPrism]] */
  @inline def asPrism: PPrism[S, T, A, B] =
    new PPrism(\/.right compose get, reverseGet)

  /** view a [[PIso]] as a [[PLens]] */
  @inline  def asLens: PLens[S, T, A, B] =
    new PLens(get, set)

}

object PIso {
  /** create a [[PIso]] using a pair of functions: one to get the target and one to get the source. */
  def apply[S, T, A, B](get: S => A)(reverseGet: B => T): PIso[S, T, A, B] =
    new PIso[S, T, A, B](get, reverseGet)

  /**
   * create a [[PIso]] between any type and itself. id is the zero element of optics composition,
   * for all optics o of type O (e.g. Lens, Iso, Prism, ...):
   * o      composeIso Iso.id == o
   * Iso.id composeO   o        == o (replace composeO by composeLens, composeIso, composePrism, ...)
   */
  def id[S, T]: PIso[S, T, S, T] =
    new PIso(identity, identity)

  /** transform an [[scalaz.Isomorphisms.Iso2]] in a [[PIso]] */
  def fromIsoFunctor[F[_], G[_], A, B](isoFunctor: F <~> G): PIso[F[A], F[B], G[A], G[B]] =
    new PIso(isoFunctor.to.apply[A], isoFunctor.from.apply[B])
}

object Iso {
  /** alias for [[PIso]] apply when S = T and A = B */
  def apply[S, A](get: S => A)(reverseGet: A => S): Iso[S, A] =
    new PIso(get, reverseGet)

  /** alias for [[PIso]] id when S = T and A = B */
  def id[S]: Iso[S, S] =
    new PIso(identity, identity)

  /** transform an [[scalaz.Isomorphisms.Iso]] in a [[Iso]] */
  def fromIsoSet[A, B](isoSet: A <=> B): Iso[A, B] =
    new PIso(isoSet.to, isoSet.from)
}
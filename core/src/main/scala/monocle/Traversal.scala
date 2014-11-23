package monocle

import scalaz.Id.Id
import scalaz.Maybe._
import scalaz.std.anyVal._
import scalaz.syntax.std.boolean._
import scalaz.syntax.tag._
import scalaz.{Applicative, Const, IList, Maybe, Monoid, Traverse}


/**
 * A [[PTraversal]] can be seen as a [[POptional]] generalised to 0 to n targets
 * where n can be infinite.
 *
 * [[PTraversal]] stands for Polymorphic Traversal as it set and modify methods change
 * a type A to B and S to T.
 * [[Traversal]] is a type alias for [[PTraversal]] restricted to monomoprhic updates:
 *
 * type Traversal[S, A] = PTraversal[S, S, A, A]
 *
 * @see TraversalLaws in monocle-law module
 *
 * @tparam S the source of a [[PTraversal]]
 * @tparam T the modified source of a [[PTraversal]]
 * @tparam A the target of a [[PTraversal]]
 * @tparam B the modified target of a [[PTraversal]]
 */
abstract class PTraversal[S, T, A, B] { self =>

  /**
   * modify polymorphically the target of a [[PTraversal]] with an [[Applicative]] function
   * all traversal methods are written in terms of modifyF
   */
  def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T]

  @inline final def foldMap[M: Monoid](f: A => M)(s: S): M =
    modifyF[Const[M, ?]](a => Const(f(a)))(s).getConst

  /**
   * get all the targets of a [[PTraversal]]
   * TODO: Shall it return a Stream as there might be an infinite number of targets?
   */
  @inline final def getAll(s: S): IList[A] =
    foldMap(IList(_))(s)

  /** find the first target of a [[PTraversal]] matching the predicate  */
  @inline final def find(p: A => Boolean)(s: S): Maybe[A] =
    foldMap(a => if(p(a)) just(a).first else empty[A].first)(s).unwrap

  /** get the first target of a [[PTraversal]] */
  @inline final def headMaybe(s: S): Maybe[A] =
    find(_ => true)(s)

  /** check if at least one target satisfies the predicate */
  @inline final def exist(p: A => Boolean)(s: S): Boolean =
    foldMap(p(_).disjunction)(s).unwrap

  /** check if all targets satisfy the predicate */
  @inline final def all(p: A => Boolean)(s: S): Boolean =
    foldMap(p(_).conjunction)(s).unwrap

  /** modify polymorphically the target of a [[PTraversal]] with a function */
  @inline final def modify(f: A => B): S => T =
    modifyF[Id](f)

  /** set polymorphically the target of a [[PTraversal]] with a value */
  @inline final def set(b: B): S => T =
    modify(_ => b)

  /****************************************************************/
  /** Compose methods between a [[PTraversal]] and another Optics */
  /****************************************************************/

  /** compose a [[PTraversal]] with a [[Fold]] */
  @inline final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    asFold composeFold other

  /** compose a [[PTraversal]] with a [[Getter]] */
  @inline final def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    asFold composeGetter other

  /** compose a [[PTraversal]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter composeSetter other

  /** compose a [[PTraversal]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    new PTraversal[S, T, C, D] {
      def modifyF[F[_]: Applicative](f: C => F[D])(s: S): F[T] =
        self.modifyF(other.modifyF(f)(_))(s)
    }

  /** compose a [[PTraversal]] with a [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): PTraversal[S, T, C, D] =
    composeTraversal(other.asTraversal)

  /** compose a [[PTraversal]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): PTraversal[S, T, C, D] =
    composeTraversal(other.asTraversal)

  /** compose a [[PTraversal]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PTraversal[S, T, C, D] =
    composeTraversal(other.asTraversal)

  /** compose a [[PTraversal]] with a [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PTraversal[S, T, C, D] =
    composeTraversal(other.asTraversal)

  /**********************************************************************/
  /** Transformation methods to view a [[PTraversal]] as another Optics */
  /**********************************************************************/

  /** view a [[PTraversal]] as a [[Fold]] */
  @inline final def asFold: Fold[S, A] =
    new Fold[S, A]{
      def foldMap[M: Monoid](f: A => M)(s: S): M =
        self.foldMap(f)(s)
    }

  /** view a [[PTraversal]] as a [[PSetter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    PSetter(modify)

}

object PTraversal {

  /** create a [[PTraversal]] from a [[Traverse]] */
  def fromTraverse[T[_]: Traverse, A, B]: PTraversal[T[A], T[B], A, B] =
    new PTraversal[T[A], T[B], A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: T[A]): F[T[B]] =
        Traverse[T].traverse(s)(f)
    }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (B, B, S) => T): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].apply2(f(get1(s)), f(get2(s)))(_set(_, _, s))
    }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (B, B, B, S) => T): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].apply3(f(get1(s)), f(get2(s)), f(get3(s)))(_set(_, _, _, s))
    }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(_set: (B, B, B, B, S) => T): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].apply4(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)))(_set(_, _, _, _, s))
    }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(_set: (B, B, B, B, B, S) => T): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].apply5(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)))(_set(_, _, _, _, _, s))
    }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(_set: (B, B, B, B, B, B, S) => T): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].apply6(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)))(_set(_, _, _, _, _, _, s))
    }

}

object Traversal {

  def apply2[S, A](get1: S => A, get2: S => A)(set: (A, A, S) => S): Traversal[S, A] =
    PTraversal.apply2(get1, get2)(set)

  def apply3[S, A](get1: S => A, get2: S => A, get3: S => A)(set: (A, A, A, S) => S): Traversal[S, A] =
    PTraversal.apply3(get1, get2, get3)(set)

  def apply4[S, A](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(set: (A, A, A, A, S) => S): Traversal[S, A] =
    PTraversal.apply4(get1, get2, get3, get4)(set)

  def apply5[S, A](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(set: (A, A, A, A, A, S) => S): Traversal[S, A] =
    PTraversal.apply5(get1, get2, get3, get4, get5)(set)

  def apply6[S, A](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(set: (A, A, A, A, A, A, S) => S): Traversal[S, A] =
    PTraversal.apply6(get1, get2, get3, get4, get5, get6)(set)

}
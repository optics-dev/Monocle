package monocle

import scalaz.{\/, Applicative, Category, Choice, Compose, Maybe, Monoid, Functor, Split}

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
 */
abstract class PLens[S, T, A, B] private[monocle]{ self =>

  /** get the target of a [[PLens]] */
  def get(s: S): A

  /** set polymorphically the target of a [[PLens]] using a function */
  def set(b: B): S => T

  /** modify polymorphically the target of a [[PLens]] using [[Functor]] function */
  def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T]

  /** modify polymorphically the target of a [[PLens]] using a function */
  def modify(f: A => B): S => T

  /** join two [[PLens]] with the same target */
  @inline final def sum[S1, T1](other: PLens[S1, T1, A, B]): PLens[S \/ S1, T \/ T1, A, B] =
    PLens[S \/ S1, T \/ T1, A, B](_.fold(self.get, other.get)){
      b => _.bimap(self.set(b), other.set(b))
    }

  /** alias for sum */
  @inline final def |||[S1, T1](other: PLens[S1, T1, A, B]): PLens[S \/ S1, T \/ T1, A, B] =
    sum(other)

  /** pair two disjoint [[PLens]] */
  @inline final def product[S1, T1, A1, B1](other: PLens[S1, T1, A1, B1]): PLens[(S, S1), (T, T1), (A, A1), (B, B1)] =
    PLens[(S, S1), (T, T1), (A, A1), (B, B1)]{
      case (s, s1) => (self.get(s), other.get(s1))
    }{ case (b, b1) => {
        case (s, s1) => (self.set(b)(s), other.set(b1)(s1))
      }
    }

  /** alias for product */
  @inline final def ***[S1, T1, A1, B1](other: PLens[S1, T1, A1, B1]): PLens[(S, S1), (T, T1), (A, A1), (B, B1)] =
    product(other)

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
    new PLens[S, T, C, D]{
      def get(s: S): C =
        other.get(self.get(s))

      def set(d: D): S => T =
        self.modify(other.set(d))

      def modifyF[F[_]: Functor](f: C => F[D])(s: S): F[T] =
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
    new Getter[S, A]{
      def get(s: S): A =
        self.get(s)
    }

  /** view a [[PLens]] as a [[PSetter]] */
  @inline final def asSetter: PSetter[S, T, A, B] =
    new PSetter[S, T, A, B]{
      def modify(f: A => B): S => T =
        self.modify(f)

      def set(b: B): S => T =
        self.set(b)
    }

  /** view a [[PLens]] as a [[PTraversal]] */
  @inline final def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PLens]] as an [[POptional]] */
  @inline final def asOptional: POptional[S, T, A, B] =
    new POptional[S, T, A, B] {
      def getOrModify(s: S): T \/ A =
        \/.right(get(s))

      def set(b: B): S => T =
        self.set(b)

      def getMaybe(s: S): Maybe[A] =
        Maybe.just(self.get(s))

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

}

object PLens extends LensInstances {
  /**
   * create a [[PLens]] using a pair of functions: one to get the target, one to set the target.
   * @see macro module for methods generating [[PLens]] with less boiler plate
   */
  def apply[S, T, A, B](_get: S => A)(_set: B => S => T): PLens[S, T, A, B] =
    new PLens[S, T, A, B]{
      def get(s: S): A =
        _get(s)

      def set(b: B): S => T =
        _set(b)

      def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
        Functor[F].map(f(_get(s)))(_set(_)(s))

      def modify(f: A => B): S => T =
       s => set(f(_get(s)))(s)
    }

}

object Lens {
  /** alias for [[PLens]] apply with a monomorphic set function */
  def apply[S, A](get: S => A)(set: A => S => S): Lens[S, A] =
    PLens(get)(set)
}

//
// Prioritized Implicits for type class instances
//

sealed abstract class LensInstances2 {
  implicit val lensCompose: Compose[Lens] = new LensCompose {}
}

sealed abstract class LensInstances1 extends LensInstances2 {
  implicit val lensCategory: Category[Lens] = new LensCategory {}
}

sealed abstract class LensInstances0 extends LensInstances1 {
  implicit val lensSplit: Split[Lens]  = new LensSplit {}
}

sealed abstract class LensInstances extends LensInstances0 {
  implicit val lensChoice: Choice[Lens] = new LensChoice {}
}

//
// Implementation traits for type class instances
//

private trait LensCompose extends Compose[Lens]{
  def compose[A, B, C](f: Lens[B, C], g: Lens[A, B]): Lens[A, C] =
    g composeLens f
}

private trait LensCategory extends Category[Lens] with LensCompose {
  def id[A]: Lens[A, A] =
    Iso.id[A].asLens
}

private trait LensSplit extends Split[Lens] with LensCompose {
  def split[A, B, C, D](f: Lens[A, B], g: Lens[C, D]): Lens[(A, C), (B, D)] =
    f product g
}

private trait LensChoice extends Choice[Lens] with LensCategory {
  def choice[A, B, C](f1: => Lens[A, C], f2: => Lens[B, C]): Lens[A \/ B, C] =
    f1 sum f2
}

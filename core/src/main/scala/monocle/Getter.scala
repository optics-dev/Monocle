package monocle

import scalaz._


/**
 * A [[Getter]] can be seen as a glorified get method between
 * a type S and a type A.
 *
 * A [[Getter]] is also a valid [[Fold]]
 *
 * @tparam S the source of a [[Getter]]
 * @tparam A the target of a [[Getter]]
 */
abstract class Getter[S, A] private[monocle]{ self =>
  /** get the target of a [[Getter]] */
  def get(s: S): A

  /** join two [[Getter]] with the same target */
  @inline final def sum[S1](other: Getter[S1, A]): Getter[S \/ S1, A] =
    Getter[S \/ S1, A](_.fold(self.get, other.get))

  /** alias for sum */
  @inline final def |||[S1](other: Getter[S1, A]): Getter[S \/ S1, A] =
    sum(other)

  /** pair two disjoint [[Getter]] */
  @inline final def product[S1, A1](other: Getter[S1, A1]): Getter[(S, S1), (A, A1)] =
    Getter[(S, S1), (A, A1)]{case (s, s1) => (self.get(s), other.get(s1))}

  /** alias for product */
  @inline final def ***[S1, A1](other: Getter[S1, A1]): Getter[(S, S1), (A, A1)] =
    product(other)

  /*************************************************************/
  /** Compose methods between a [[Getter]] and another Optics  */
  /*************************************************************/

  /** compose a [[Getter]] with a [[Fold]] */
  @inline def composeFold[B](other: Fold[A, B]): Fold[S, B] =
    asFold composeFold other

  /** compose a [[Getter]] with a [[Getter]] */
  @inline def composeGetter[B](other: Getter[A, B]): Getter[S, B] =
    new Getter[S, B]{
      def get(s: S): B =
        other.get(self.get(s))
    }

  /** compose a [[Getter]] with a [[PTraversal]] */
  @inline def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    asFold composeTraversal other

  /** compose a [[Getter]] with a [[POptional]] */
  @inline def composeOptional[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    asFold composeOptional other

  /** compose a [[Getter]] with a [[PPrism]] */
  @inline def composePrism[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    asFold composePrism other

  /** compose a [[Getter]] with a [[PLens]] */
  @inline def composeLens[B, C, D](other: PLens[A, B, C, D]): Getter[S, C] =
    composeGetter(other.asGetter)

  /** compose a [[Getter]] with a [[PIso]] */
  @inline def composeIso[B, C, D](other: PIso[A, B, C, D]): Getter[S, C] =
    composeGetter(other.asGetter)

  /********************************************/
  /** Experimental aliases of compose methods */
  /********************************************/

  /** alias to composeTraversal */
  @inline def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline def ^|-?[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    composeOptional(other)

  /** alias to composePrism */
  @inline def ^<-?[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    composePrism(other)

  /** alias to composeLens */
  @inline def ^|->[B, C, D](other: PLens[A, B, C, D]): Getter[S, C] =
    composeLens(other)

  /** alias to composeIso */
  @inline def ^<->[B, C, D](other: PIso[A, B, C, D]): Getter[S, C] =
    composeIso(other)

  /******************************************************************/
  /** Transformation methods to view a [[Getter]] as another Optics */
  /******************************************************************/

  /** view a [[Getter]] with a [[Fold]] */
  @inline def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M =
      f(get(s))
  }

}

object Getter extends GetterInstances {
  def apply[S, A](_get: S => A): Getter[S, A] =
    new Getter[S, A]{
      def get(s: S): A =
        _get(s)
    }
}

//
// Prioritized Implicits for type class instances
//

sealed abstract class GetterInstances3 {
  implicit val getterCompose: Compose[Getter] = new GetterCompose {}
}

sealed abstract class GetterInstances2 extends GetterInstances3 {
  implicit val getterCategory: Category[Getter] = new GetterCategory {}
}

sealed abstract class GetterInstances1 extends GetterInstances2 {
  implicit val getterSplit: Split[Getter] = new GetterSplit {}
}

sealed abstract class GetterInstances0 extends GetterInstances1 {
  implicit val getterProfunctor: Profunctor[Getter] = new GetterProfunctor {}
  implicit val getterChoice: Choice[Getter] = new GetterChoice {}
}

sealed abstract class GetterInstances extends GetterInstances0 {
  implicit val getterArrow: Arrow[Getter] = new GetterArrow {}
}

//
// Implementation traits for type class instances
//

private trait GetterCompose extends Compose[Getter]{
  def compose[A, B, C](f: Getter[B, C], g: Getter[A, B]): Getter[A, C] =
    g composeGetter f
}

private trait GetterCategory extends Category[Getter] with GetterCompose {
  def id[A]: Getter[A, A] =
    Iso.id[A].asGetter
}

private trait GetterSplit extends Split[Getter] with GetterCompose {
  def split[A, B, C, D](f: Getter[A, B], g: Getter[C, D]): Getter[(A, C), (B, D)] =
    f product g
}

private trait GetterChoice extends Choice[Getter] with GetterCategory {
  def choice[A, B, C](f1: => Getter[A, C], f2: => Getter[B, C]): Getter[A \/ B, C] =
    f1 sum f2
}

private trait GetterProfunctor extends Profunctor[Getter] {
  override def dimap[A, B, C, D](fab: Getter[A, B])(f: C => A)(g: B => D): Getter[C, D] =
    Getter(g compose fab.get _ compose f)

  def mapfst[A, B, C](fab: Getter[A, B])(f: C => A): Getter[C, B] =
    Getter(fab.get _ compose f)

  def mapsnd[A, B, C](fab: Getter[A, B])(f: B => C): Getter[A, C] =
    Getter(f compose fab.get)
}

private trait GetterArrow extends Arrow[Getter] with GetterCategory {
  def arr[A, B](f: A => B): Getter[A, B] =
    Getter(f)

  def first[A, B, C](f: Getter[A, B]): Getter[(A, C), (B, C)] =
    Getter[(A, C), (B, C)]{case (a, c) => (f.get(a), c)}
}
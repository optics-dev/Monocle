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

sealed abstract class GetterInstances1 {
  implicit val getterCompose: Compose[Getter] = new GetterCompose {}
}

sealed abstract class GetterInstances0 {
  implicit val getterCategory: Category[Getter] = new GetterCategory {}
}

sealed abstract class GetterInstances extends GetterInstances0 {
  implicit val getterChoice: Choice[Getter] = new GetterChoice {}
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

private trait GetterChoice extends Choice[Getter] with GetterCategory {
  def choice[A, B, C](f1: => Getter[A, C], f2: => Getter[B, C]): Getter[A \/ B, C] =
    new Getter[A \/ B, C]{
      def get(s: A \/ B): C =
        s.fold(f1.get, f2.get)
    }
}
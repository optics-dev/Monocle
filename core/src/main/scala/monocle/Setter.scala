package monocle

import scalaz.{Choice, Functor, \/}

/**
 * A [[PSetter]] is a generalisation of [[Functor]] map:
 *  - `map:    (A => B) => F[A] => F[B]`
 *  - `modify: (A => B) => S    => T`
 *
 * [[PSetter]] stands for Polymorphic Setter as it set and modify methods change
 * a type `A` to `B` and `S` to `T`.
 * [[Setter]] is a type alias for [[PSetter]] restricted to monomorphic updates:
 * {{{
 * type Setter[S, A] = PSetter[S, S, A, A]
 * }}}
 *
 * [[PTraversal]], [[POptional]], [[PPrism]], [[PLens]] and [[PIso]] are valid [[PSetter]]
 *
 * @see [[monocle.law.SetterLaws]]
 *
 * @tparam S the source of a [[PSetter]]
 * @tparam T the modified source of a [[PSetter]]
 * @tparam A the target of a [[PSetter]]
 * @tparam B the modified target of a [[PSetter]]
 */
abstract class PSetter[S, T, A, B] extends Serializable { self =>

  /** modify polymorphically the target of a [[PSetter]] with a function */
  def modify(f: A => B): S => T

  /** set polymorphically the target of a [[PSetter]] with a value */
  def set(b: B): S => T

  /** join two [[PSetter]] with the same target */
  @inline final def sum[S1, T1](other: PSetter[S1, T1, A, B]): PSetter[S \/ S1, T \/ T1, A, B] =
    PSetter[S \/ S1, T \/ T1, A, B](
      b => _.bimap(self.modify(b), other.modify(b))
    )

  /*************************************************************/
  /** Compose methods between a [[PSetter]] and another Optics */
  /*************************************************************/

  /** compose a [[PSetter]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    new PSetter[S, T, C, D]{
      def modify(f: C => D): S => T =
        self.modify(other.modify(f))

      def set(d: D): S => T =
        self.modify(other.set(d))
    }

  /** compose a [[PSetter]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /** compose a [[PSetter]] with a [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PSetter[S, T, C, D] =
    composeSetter(other.asSetter)

  /********************************************/
  /** Experimental aliases of compose methods */
  /********************************************/

  /** alias to composeTraversal */
  @inline final def ^|->>[C, D](other: PTraversal[A, B, C, D]): PSetter[S, T, C, D] =
    composeTraversal(other)

  /** alias to composeOptional */
  @inline final def ^|-?[C, D](other: POptional[A, B, C, D]): PSetter[S, T, C, D] =
    composeOptional(other)

  /** alias to composePrism */
  @inline final def ^<-?[C, D](other: PPrism[A, B, C, D]): PSetter[S, T, C, D] =
    composePrism(other)

  /** alias to composeLens */
  @inline final def ^|->[C, D](other: PLens[A, B, C, D]): PSetter[S, T, C, D] =
    composeLens(other)

  /** alias to composeIso */
  @inline final def ^<->[C, D](other: PIso[A, B, C, D]): PSetter[S, T, C, D] =
    composeIso(other)

}

object PSetter extends SetterInstances {
  def id[S, T]: PSetter[S, T, S, T] =
    PIso.id[S, T].asSetter

  def codiagonal[S, T]: PSetter[S \/ S, T \/ T, S, T] =
    PSetter[S \/ S, T \/ T, S, T](f => _.bimap(f,f))

  /** create a [[PSetter]] using modify function */
  def apply[S, T, A, B](_modify: (A => B) => S => T): PSetter[S, T, A, B] =
    new PSetter[S, T, A, B]{
      def modify(f: A => B): S => T =
        _modify(f)

      def set(b: B): S => T =
        _modify(_ => b)
    }

  /** create a [[PSetter]] from a [[Functor]] */
  def fromFunctor[F[_]: Functor, A, B]: PSetter[F[A], F[B], A, B] =
    PSetter[F[A], F[B], A, B](f => Functor[F].map(_)(f))

}

object Setter {
  def id[A]: Setter[A, A] =
    Iso.id[A].asSetter

  def codiagonal[S]: Setter[S \/ S, S] =
    PSetter.codiagonal

  /** alias for [[PSetter]] apply with a monomorphic modify function */
  def apply[S, A](modify: (A => A) => S => S): Setter[S, A] =
    PSetter(modify)
}

sealed abstract class SetterInstances {
  implicit val SetterChoice: Choice[Setter] = new Choice[Setter] {
    def compose[A, B, C](f: Setter[B, C], g: Setter[A, B]): Setter[A, C] =
      g composeSetter f

    def id[A]: Setter[A, A] =
      Setter.id

    def choice[A, B, C](f1: => Setter[A, C], f2: => Setter[B, C]): Setter[A \/ B, C] =
      f1 sum f2
  }
}
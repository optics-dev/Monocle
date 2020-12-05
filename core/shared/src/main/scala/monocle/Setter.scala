package monocle

import cats.{Contravariant, Eq, Functor}
import cats.arrow.Choice
import cats.arrow.Profunctor
import cats.syntax.either._
import monocle.function.{At, Each}

/** A [[PSetter]] is a generalisation of Functor map:
  *  - `map:    (A => B) => F[A] => F[B]`
  *  - `modify: (A => B) => S    => T`
  *
  * [[PSetter]] stands for Polymorphic Setter as it replace and modify methods change
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

  /** replace polymorphically the target of a [[PSetter]] with a value */
  def replace(b: B): S => T

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: B): S => T = replace(b)

  /** join two [[PSetter]] with the same target */
  @inline final def choice[S1, T1](other: PSetter[S1, T1, A, B]): PSetter[Either[S, S1], Either[T, T1], A, B] =
    PSetter[Either[S, S1], Either[T, T1], A, B](b => _.bimap(self.modify(b), other.modify(b)))

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): PSetter[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]] composePrism (std.option.pSome)

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): PSetter[S, T, A1, B1] =
    evB.substituteCo[PSetter[S, T, A1, *]](evA.substituteCo[PSetter[S, T, *, B]](this))

  /** compose a [[PSetter]] with another [[PSetter]] */
  final def andThen[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    new PSetter[S, T, C, D] {
      def modify(f: C => D): S => T =
        self.modify(other.modify(f))

      def replace(d: D): S => T =
        self.modify(other.replace(d))
    }

  /** compose a [[PSetter]] with a [[PTraversal]] */
  final def andThen[C, D](other: PTraversal[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other.asSetter)

  /** compose a [[PSetter]] with a [[POptional]] */
  final def andThen[C, D](other: POptional[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other.asSetter)

  /** compose a [[PSetter]] with a [[PPrism]] */
  final def andThen[C, D](other: PPrism[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other.asSetter)

  /** compose a [[PSetter]] with a [[PLens]] */
  final def andThen[C, D](other: PLens[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other.asSetter)

  /** compose a [[PSetter]] with a [[PIso]] */
  final def andThen[C, D](other: PIso[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other.asSetter)

  /** compose a [[PSetter]] with a [[PSetter]] */
  @inline final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** compose a [[PSetter]] with a [[PTraversal]] */
  @inline final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** compose a [[PSetter]] with a [[POptional]] */
  @inline final def composeOptional[C, D](other: POptional[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** compose a [[PSetter]] with a [[PPrism]] */
  @inline final def composePrism[C, D](other: PPrism[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** compose a [[PSetter]] with a [[PLens]] */
  @inline final def composeLens[C, D](other: PLens[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** compose a [[PSetter]] with a [[PIso]] */
  @inline final def composeIso[C, D](other: PIso[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** *****************************************
    */
  /** Experimental aliases of compose methods */
  /** *****************************************
    */
  /** alias to composeTraversal */
  @inline final def ^|->>[C, D](other: PTraversal[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** alias to composeOptional */
  @inline final def ^|-?[C, D](other: POptional[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** alias to composePrism */
  @inline final def ^<-?[C, D](other: PPrism[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** alias to composeLens */
  @inline final def ^|->[C, D](other: PLens[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** alias to composeIso */
  @inline final def ^<->[C, D](other: PIso[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)
}

object PSetter extends SetterInstances {
  def id[S, T]: PSetter[S, T, S, T] =
    PIso.id[S, T].asSetter

  def codiagonal[S, T]: PSetter[Either[S, S], Either[T, T], S, T] =
    PSetter[Either[S, S], Either[T, T], S, T](f => _.bimap(f, f))

  /** create a [[PSetter]] using modify function */
  def apply[S, T, A, B](_modify: (A => B) => S => T): PSetter[S, T, A, B] =
    new PSetter[S, T, A, B] {
      def modify(f: A => B): S => T =
        _modify(f)

      def replace(b: B): S => T =
        _modify(_ => b)
    }

  /** create a [[PSetter]] from a cats.Functor */
  def fromFunctor[F[_], A, B](implicit F: Functor[F]): PSetter[F[A], F[B], A, B] =
    PSetter[F[A], F[B], A, B](f => F.map(_)(f))

  /** create a [[PSetter]] from a Contravariant functor */
  def fromContravariant[F[_], A, B](implicit F: Contravariant[F]): PSetter[F[B], F[A], A, B] =
    PSetter[F[B], F[A], A, B](f => F.contramap(_)(f))

  /** create a [[PSetter]] from a Profunctor */
  def fromProfunctor[P[_, _], A, B, C](implicit P: Profunctor[P]): PSetter[P[B, C], P[A, C], A, B] =
    PSetter[P[B, C], P[A, C], A, B](f => P.lmap(_)(f))

  implicit def setterSyntax[S, A](self: Setter[S, A]): SetterSyntax[S, A] =
    new SetterSyntax(self)
}

object Setter {
  def id[A]: Setter[A, A] =
    Iso.id[A].asSetter

  def codiagonal[S]: Setter[Either[S, S], S] =
    PSetter.codiagonal

  /** [[Setter]] that points to nothing */
  def void[S, A]: Setter[S, A] =
    Optional.void.asSetter

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

    def choice[A, B, C](f1: Setter[A, C], f2: Setter[B, C]): Setter[Either[A, B], C] =
      f1 choice f2
  }
}

/** Extension methods for monomorphic Setter
  */
final case class SetterSyntax[S, A](private val self: Setter[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): Setter[S, C] =
    self composeTraversal evEach.each

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Setter[S, A1] =
    self.adapt[Option[A1], Option[A1]] composeIso (std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): Setter[S, A1] =
    self composeLens evAt.at(i)
}

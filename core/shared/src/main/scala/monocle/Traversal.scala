package monocle

import cats.{Applicative, Functor, Id, Monoid, Parallel, Traverse}
import cats.arrow.Choice
import cats.data.Const
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}
import cats.catsInstancesForId
import cats.syntax.traverse._

/** A [[PTraversal]] can be seen as a [[POptional]] generalised to 0 to n targets
  * where n can be infinite.
  *
  * [[PTraversal]] stands for Polymorphic Traversal as it replace and modify methods change
  * a type `A` to `B` and `S` to `T`.
  * [[Traversal]] is a type alias for [[PTraversal]] restricted to monomorphic updates:
  * {{{
  * type Traversal[S, A] = PTraversal[S, S, A, A]
  * }}}
  *
  * @see [[monocle.law.TraversalLaws]]
  *
  * @tparam S the source of a [[PTraversal]]
  * @tparam T the modified source of a [[PTraversal]]
  * @tparam A the target of a [[PTraversal]]
  * @tparam B the modified target of a [[PTraversal]]
  */
trait PTraversal[S, T, A, B] extends PSetter[S, T, A, B] with Fold[S, A] { self =>

  /** modify polymorphically the target of a [[PTraversal]] with an Applicative function
    * all traversal methods are written in terms of modifyA
    */
  def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T]

  // TODO improve performance
  def iterator(from: S): Iterator[A] =
    modifyA[Const[List[A], *]](a => Const(List(a)))(from).getConst.iterator

  /** modify polymorphically the target of a [[PTraversal]] with a function */
  def modify(f: A => B): S => T =
    modifyA[Id](f)

  /** replace polymorphically the target of a [[PTraversal]] with a value */
  def replace(b: B): S => T =
    modify(_ => b)

  /** join two [[PTraversal]] with the same target */
  def choice[S1, T1](other: PTraversal[S1, T1, A, B]): PTraversal[Either[S, S1], Either[T, T1], A, B] =
    new PTraversal[Either[S, S1], Either[T, T1], A, B] {
      def modifyA[F[_]: Applicative](f: A => F[B])(s: Either[S, S1]): F[Either[T, T1]] =
        s.fold(
          s => Functor[F].map(self.modifyA(x => f(x))(s))(Either.left(_)),
          s1 => Functor[F].map(other.modifyA(f)(s1))(Either.right)
        )
    }

  /** [[PTraversal.modifyA]] for a `Parallel` applicative functor.
    */
  def parModifyF[F[_]](f: A => F[B])(s: S)(implicit F: Parallel[F]): F[T] =
    F.sequential(
      modifyA(a => F.parallel(f(a)))(s)(F.applicative)
    )

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): PTraversal[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): PTraversal[S, T, A1, B1] =
    evB.substituteCo[PTraversal[S, T, A1, *]](evA.substituteCo[PTraversal[S, T, *, B]](this))

  /** compose a [[PTraversal]] with another [[PTraversal]] */
  def andThen[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    new PTraversal[S, T, C, D] {
      def modifyA[F[_]: Applicative](f: C => F[D])(s: S): F[T] =
        self.modifyA(other.modifyA(f)(_))(s)
    }

  /** *******************************************************************
    */
  /** Transformation methods to view a [[PTraversal]] as another Optics */
  /** *******************************************************************
    */
  /** view a [[PTraversal]] as a [[Fold]] */
  def asFold: Fold[S, A] = this

  /** view a [[PTraversal]] as a [[PSetter]] */
  def asSetter: PSetter[S, T, A, B] = this
}

object PTraversal extends TraversalInstances {
  @deprecated("use PIso.id", since = "3.0.0-M2")
  def id[S, T]: PTraversal[S, T, S, T] =
    PIso.id[S, T]

  def codiagonal[S, T]: PTraversal[Either[S, S], Either[T, T], S, T] =
    new PTraversal[Either[S, S], Either[T, T], S, T] {
      def modifyA[F[_]: Applicative](f: S => F[T])(s: Either[S, S]): F[Either[T, T]] =
        s.bimap(f, f)
          .fold(Applicative[F].map(_)(Either.left), Applicative[F].map(_)(Either.right))
    }

  /** create a [[PTraversal]] from a Traverse */
  def fromTraverse[T[_]: Traverse, A, B]: PTraversal[T[A], T[B], A, B] =
    new PTraversal[T[A], T[B], A, B] {
      def modifyA[F[_]: Applicative](f: A => F[B])(s: T[A]): F[T[B]] =
        Traverse[T].traverse(s)(f)
    }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (B, B, S) => T): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].map2(f(get1(s)), f(get2(s)))(_set(_, _, s))
    }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (B, B, B, S) => T): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].map3(f(get1(s)), f(get2(s)), f(get3(s)))(_set(_, _, _, s))
    }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(
    _set: (B, B, B, B, S) => T
  ): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].map4(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)))(_set(_, _, _, _, s))
    }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(
    _set: (B, B, B, B, B, S) => T
  ): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].map5(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)))(_set(_, _, _, _, _, s))
    }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(
    _set: (B, B, B, B, B, B, S) => T
  ): PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        Applicative[F].map6(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)))(
          _set(_, _, _, _, _, _, s)
        )
    }

  implicit def pTraversalSyntax[S, T, A, B](self: PTraversal[S, T, A, B]): PTraversalSyntax[S, T, A, B] =
    new PTraversalSyntax(self)

  implicit def traversalSyntax[S, A](self: Traversal[S, A]): TraversalSyntax[S, A] =
    new TraversalSyntax(self)
}

object Traversal {
  @deprecated("use Iso.id", since = "3.0.0-M2")
  def id[A]: Traversal[A, A] =
    Iso.id[A]

  def codiagonal[S, T]: Traversal[Either[S, S], S] =
    PTraversal.codiagonal

  /** create a [[PTraversal]] from a Traverse */
  def fromTraverse[T[_]: Traverse, A]: Traversal[T[A], A] =
    PTraversal.fromTraverse

  /** [[Traversal]] that points to nothing */
  @deprecated("use Optional.void", since = "3.0.0-M2")
  def void[S, A]: Traversal[S, A] =
    Optional.void

  def apply2[S, A](get1: S => A, get2: S => A)(set: (A, A, S) => S): Traversal[S, A] =
    PTraversal.apply2(get1, get2)(set)

  def apply3[S, A](get1: S => A, get2: S => A, get3: S => A)(set: (A, A, A, S) => S): Traversal[S, A] =
    PTraversal.apply3(get1, get2, get3)(set)

  def apply4[S, A](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(set: (A, A, A, A, S) => S): Traversal[S, A] =
    PTraversal.apply4(get1, get2, get3, get4)(set)

  def apply5[S, A](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(
    set: (A, A, A, A, A, S) => S
  ): Traversal[S, A] =
    PTraversal.apply5(get1, get2, get3, get4, get5)(set)

  def apply6[S, A](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(
    set: (A, A, A, A, A, A, S) => S
  ): Traversal[S, A] =
    PTraversal.apply6(get1, get2, get3, get4, get5, get6)(set)

  /** Merge multiple Optionals together.
    * All Optional must target different piece of data otherwise the Traversal doesn't respect all properties.
    * See this thread for more details: https://github.com/julien-truffaut/Monocle/issues/379#issuecomment-236374838.
    */
  def applyN[S, A](xs: Optional[S, A]*): Traversal[S, A] =
    new PTraversal[S, S, A, A] {
      def modifyA[F[_]: Applicative](f: A => F[A])(s: S): F[S] =
        xs.foldLeft(Applicative[F].pure(s))((fs, lens) =>
          Applicative[F].map2(lens.getOption(s).traverse(f), fs) {
            case (None, s)    => s
            case (Some(a), s) => lens.replace(a)(s)
          }
        )
    }
}

sealed abstract class TraversalInstances {
  implicit val traversalChoice: Choice[Traversal] = new Choice[Traversal] {
    def compose[A, B, C](f: Traversal[B, C], g: Traversal[A, B]): Traversal[A, C] =
      g.andThen(f)

    def id[A]: Traversal[A, A] =
      Iso.id

    def choice[A, B, C](f1: Traversal[A, C], f2: Traversal[B, C]): Traversal[Either[A, B], C] =
      f1 choice f2
  }
}

final case class PTraversalSyntax[S, T, A, B](private val self: PTraversal[S, T, A, B]) extends AnyVal {

  /** compose a [[PTraversal]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[PTraversal]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[PTraversal]] with a [[PSetter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PTraversal]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PTraversal]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PTraversal]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PTraversal]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PTraversal]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)
}

/** Extension methods for monomorphic Traversal
  */
final case class TraversalSyntax[S, A](private val self: Traversal[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): Traversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Traversal[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Traversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Traversal[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): Traversal[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Traversal[S, A1] =
    self.andThen(evIndex.index(i))
}

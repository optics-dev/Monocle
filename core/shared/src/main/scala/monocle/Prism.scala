package monocle

import cats.{Applicative, Eq, Traverse}
import cats.arrow.Category
import cats.evidence.{<~<, Is}
import cats.instances.option._
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}

/** A [[PPrism]] can be seen as a pair of functions:
  *   - `getOrModify: S => Either[T, A]`
  *   - `reverseGet : B => T`
  *
  * A [[PPrism]] could also be defined as a weaker [[PIso]] where get can fail.
  *
  * Typically a [[PPrism]] or [[Prism]] encodes the relation between a Sum or CoProduct type (e.g. sealed trait) and one
  * of its element.
  *
  * [[PPrism]] stands for Polymorphic Prism as it replace and modify methods change a type `A` to `B` and `S` to `T`.
  * [[Prism]] is a type alias for [[PPrism]] where the type of target cannot be modified:
  * {{{
  * type Prism[S, A] = PPrism[S, S, A, A]
  * }}}
  *
  * A [[PPrism]] is also a valid [[Fold]], [[POptional]], [[PTraversal]] and [[PSetter]]
  *
  * @see
  *   [[monocle.law.PrismLaws]]
  *
  * @tparam S
  *   the source of a [[PPrism]]
  * @tparam T
  *   the modified source of a [[PPrism]]
  * @tparam A
  *   the target of a [[PPrism]]
  * @tparam B
  *   the modified target of a [[PPrism]]
  */
trait PPrism[S, T, A, B] extends POptional[S, T, A, B] { self =>

  /** get the modified source of a [[PPrism]] */
  def reverseGet(b: B): T

  /** modify polymorphically the target of a [[PPrism]] with an Applicative function */
  def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    getOrModify(s).fold(
      t => Applicative[F].pure(t),
      a => Applicative[F].map(f(a))(reverseGet)
    )

  /** modify polymorphically the target of a [[PPrism]] with a function */
  override def modify(f: A => B): S => T =
    getOrModify(_).fold(identity, a => reverseGet(f(a)))

  override def replace(b: B): S => T =
    modify(_ => b)

  /** create a [[Getter]] from the modified target to the modified source of a [[PPrism]] */
  def re: Getter[B, T] =
    Getter(reverseGet)

  @deprecated("no replacement", since = "3.0.0-M4")
  override def first[C]: PPrism[(S, C), (T, C), (A, C), (B, C)] =
    PPrism[(S, C), (T, C), (A, C), (B, C)] { case (s, c) =>
      getOrModify(s).bimap(_ -> c, _ -> c)
    } { case (b, c) =>
      (reverseGet(b), c)
    }

  @deprecated("no replacement", since = "3.0.0-M4")
  override def second[C]: PPrism[(C, S), (C, T), (C, A), (C, B)] =
    PPrism[(C, S), (C, T), (C, A), (C, B)] { case (c, s) =>
      getOrModify(s).bimap(c -> _, c -> _)
    } { case (c, b) =>
      (c, reverseGet(b))
    }

  @deprecated("no replacement", since = "3.0.0-M4")
  override def left[C]: PPrism[Either[S, C], Either[T, C], Either[A, C], Either[B, C]] =
    PPrism[Either[S, C], Either[T, C], Either[A, C], Either[B, C]](
      _.fold(getOrModify(_).bimap(Either.left, Either.left), c => Either.right(Either.right(c)))
    )(_.leftMap(reverseGet))

  @deprecated("no replacement", since = "3.0.0-M4")
  override def right[C]: PPrism[Either[C, S], Either[C, T], Either[C, A], Either[C, B]] =
    PPrism[Either[C, S], Either[C, T], Either[C, A], Either[C, B]](
      _.fold(c => Either.right(Either.left(c)), getOrModify(_).bimap(Either.right, Either.right))
    )(_.map(reverseGet))

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): PPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): PPrism[S, T, A1, B1] =
    evB.substituteCo[PPrism[S, T, A1, *]](evA.substituteCo[PPrism[S, T, *, B]](this))

  /** compose a [[PPrism]] with another [[PPrism]] */
  def andThen[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    new PPrism[S, T, C, D] {
      def getOrModify(s: S): Either[T, C] =
        self
          .getOrModify(s)
          .flatMap(a => other.getOrModify(a).bimap(self.replace(_)(s), identity))

      def reverseGet(d: D): T =
        self.reverseGet(other.reverseGet(d))

      def getOption(s: S): Option[C] =
        self.getOption(s) flatMap other.getOption
    }

  /** ***************************************************************
    */
  /** Transformation methods to view a [[PPrism]] as another Optics */
  /** ***************************************************************
    */
  /** view a [[PPrism]] as a [[Fold]] */

  /** view a [[PPrism]] as a [[POptional]] */
  def asOptional: POptional[S, T, A, B] = this

  /** **********************************************************************
    */
  /** Apply methods to treat a [[PPrism]] as smart constructors for type T */
  /** **********************************************************************
    */
  def apply()(implicit ev: Is[B, Unit]): T =
    ev.substitute[PPrism[S, T, A, *]](self).reverseGet(())

  def apply(b: B): T = reverseGet(b)

  def apply[C, D](c: C, d: D)(implicit ev: (C, D) <~< B): T = apply(ev((c, d)))

  def apply[C, D, E](c: C, d: D, e: E)(implicit ev: (C, D, E) <~< B): T =
    apply(ev((c, d, e)))

  def apply[C, D, E, F](c: C, d: D, e: E, f: F)(implicit ev: (C, D, E, F) <~< B): T =
    apply(ev((c, d, e, f)))

  def apply[C, D, E, F, G](c: C, d: D, e: E, f: F, g: G)(implicit ev: (C, D, E, F, G) <~< B): T =
    apply(ev((c, d, e, f, g)))

  def apply[C, D, E, F, G, H](c: C, d: D, e: E, f: F, g: G, h: H)(implicit ev: (C, D, E, F, G, H) <~< B): T =
    apply(ev((c, d, e, f, g, h)))

  def unapply(obj: S): Option[A] = getOption(obj)
}

object PPrism extends PrismInstances {
  @deprecated("use PIso.id", since = "3.0.0-M2")
  def id[S, T]: PPrism[S, T, S, T] =
    PIso.id[S, T]

  /** create a [[PPrism]] using the canonical functions: getOrModify and reverseGet */
  def apply[S, T, A, B](_getOrModify: S => Either[T, A])(_reverseGet: B => T): PPrism[S, T, A, B] =
    new PPrism[S, T, A, B] {
      def getOrModify(s: S): Either[T, A] =
        _getOrModify(s)

      def reverseGet(b: B): T =
        _reverseGet(b)

      def getOption(s: S): Option[A] =
        _getOrModify(s).toOption
    }

  implicit def pPrismSyntax[S, T, A, B](self: PPrism[S, T, A, B]): PPrismSyntax[S, T, A, B] =
    new PPrismSyntax(self)

  implicit def prismSyntax[S, A](self: Prism[S, A]): PrismSyntax[S, A] =
    new PrismSyntax(self)
}

object Prism {
  @deprecated("use Iso.id", since = "3.0.0-M2")
  def id[A]: Prism[A, A] =
    Iso.id[A]

  /** alias for [[PPrism]] apply restricted to monomorphic update */
  def apply[S, A](_getOption: S => Option[A])(_reverseGet: A => S): Prism[S, A] =
    new Prism[S, A] {
      def getOrModify(s: S): Either[S, A] =
        _getOption(s).fold[Either[S, A]](Either.left(s))(Either.right)

      def reverseGet(b: A): S =
        _reverseGet(b)

      def getOption(s: S): Option[A] =
        _getOption(s)
    }

  /** Create a Prism using a partial function rather than Option. */
  def partial[S, A](get: PartialFunction[S, A])(reverseGet: A => S): Prism[S, A] =
    Prism[S, A](get.lift)(reverseGet)

  /** a [[Prism]] that checks for equality with a given value */
  def only[A](a: A)(implicit A: Eq[A]): Prism[A, Unit] =
    Prism[A, Unit](a2 => if (A.eqv(a, a2)) Some(()) else None)(_ => a)
}

sealed abstract class PrismInstances {
  implicit val prismCategory: Category[Prism] = new Category[Prism] {
    def id[A]: Prism[A, A] =
      Iso.id

    def compose[A, B, C](f: Prism[B, C], g: Prism[A, B]): Prism[A, C] =
      g.andThen(f)
  }
}

final case class PPrismSyntax[S, T, A, B](private val self: PPrism[S, T, A, B]) extends AnyVal {

  /** compose a [[PPrism]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[PPrism]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[PPrism]] with a [[PSetter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PPrism]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PPrism]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PPrism]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PPrism]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PPrism]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): PPrism[S, T, C, D] =
    self.andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    self.andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): PPrism[S, T, C, D] =
    self.andThen(other)
}

final case class PrismSyntax[S, A](private val self: Prism[S, A]) extends AnyVal {

  /** lift a [[Prism]] such as it only matches if all elements of `F[S]` are getOrModify */
  def below[F[_]](implicit F: Traverse[F]): Prism[F[S], F[A]] =
    Prism[F[S], F[A]](F.traverse(_)(self.getOption))(F.map(_)(self.reverseGet))

  def each[C](implicit evEach: Each[A, C]): Traversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate. This combinator can break the fusion property see
    * Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Optional[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Traversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Prism[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): Optional[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Optional[S, A1] =
    self.andThen(evIndex.index(i))
}

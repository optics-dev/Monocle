package monocle

import cats.{Applicative, Eq}
import cats.arrow.Choice
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}

/** A [[POptional]] can be seen as a pair of functions:
  *  - `getOrModify: S      => Either[T, A]`
  *  - `replace    : (B, S) => T`
  *
  * A [[POptional]] could also be defined as a weaker [[PLens]] and
  * weaker [[PPrism]]
  *
  * [[POptional]] stands for Polymorphic Optional as it replace and modify methods change
  * a type `A` to `B` and `S` to `T`.
  * [[Optional]] is a type alias for [[POptional]] restricted to monomorphic updates:
  * {{{
  * type Optional[S, A] = POptional[S, S, A, A]
  * }}}
  *
  * @see [[monocle.law.OptionalLaws]]
  *
  * @tparam S the source of a [[POptional]]
  * @tparam T the modified source of a [[POptional]]
  * @tparam A the target of a [[POptional]]
  * @tparam B the modified target of a [[POptional]]
  */
trait POptional[-S, +T, +A, -B] extends PTraversal[S, T, A, B] { self =>

  /** get the target of a [[POptional]] or return the original value while allowing the type to change if it does not match */
  def getOrModify(s: S): Either[T, A]

  /** get the modified source of a [[POptional]] */
  def replace(b: B): S => T

  /** get the target of a [[POptional]] or nothing if there is no target */
  def getOption(s: S): Option[A]

  /** modify polymorphically the target of a [[POptional]] with a function */
  def modify(f: A => B): S => T

  /** modify polymorphically the target of a [[POptional]] with a function.
    * return empty if the [[POptional]] is not matching
    */
  def modifyOption(f: A => B): S => Option[T] =
    s => getOption(s).map(a => replace(f(a))(s))

  /** replace polymorphically the target of a [[POptional]] with a value.
    * return empty if the [[POptional]] is not matching
    */
  def replaceOption(b: B): S => Option[T] =
    modifyOption(_ => b)

  /** alias to replaceOption */
  @deprecated("use replaceOption instead", since = "3.0.0-M1")
  def setOption(b: B): S => Option[T] =
    replaceOption(b)

  /** check if there is no target */
  override def isEmpty(s: S): Boolean =
    getOption(s).isEmpty

  /** check if there is a target */
  override def nonEmpty(s: S): Boolean =
    getOption(s).isDefined

  /** find if the target satisfies the predicate */
  override def find(p: A => Boolean): S => Option[A] =
    getOption(_).flatMap(a => Some(a).filter(p))

  /** check if there is a target and it satisfies the predicate */
  override def exist(p: A => Boolean): S => Boolean =
    getOption(_).fold(false)(p)

  /** check if there is no target or the target satisfies the predicate */
  override def all(p: A => Boolean): S => Boolean =
    getOption(_).fold(true)(p)

  /** fall-back to another [[POptional]] in case this one doesn't match */
  def orElse[S1 <: S, T1 >: T, A1 >: A, B1 <: B](other: POptional[S1, T1, A1, B1]): POptional[S1, T1, A1, B1] =
    POptional[S1, T1, A1, B1](from => self.getOrModify(from).orElse(other.getOrModify(from)))(to =>
      from => self.replaceOption(to)(from).getOrElse(other.replace(to)(from))
    )

  /** join two [[POptional]] with the same target */
  def choice[S1, T1, A1 >: A, B1 <: B](other: POptional[S1, T1, A1, B1]): POptional[Either[S, S1], Either[T, T1], A1, B1] =
    POptional[Either[S, S1], Either[T, T1], A1, B1](
      _.fold(self.getOrModify(_).leftMap(Either.left), other.getOrModify(_).leftMap(Either.right))
    )(b => _.bimap(self.replace(b), other.replace(b)))

  def first[C]: POptional[(S, C), (T, C), (A, C), (B, C)] =
    POptional[(S, C), (T, C), (A, C), (B, C)] { case (s, c) =>
      getOrModify(s).bimap(_ -> c, _ -> c)
    } {
      case (b, c) => { case (s, c2) =>
        replaceOption(b)(s).fold(replace(b)(s) -> c2)(_ -> c)
      }
    }

  def second[C]: POptional[(C, S), (C, T), (C, A), (C, B)] =
    POptional[(C, S), (C, T), (C, A), (C, B)] { case (c, s) =>
      getOrModify(s).bimap(c -> _, c -> _)
    } {
      case (c, b) => { case (c2, s) =>
        replaceOption(b)(s).fold(c2 -> replace(b)(s))(c -> _)
      }
    }

  override def some[A1, B1](implicit ev1: A <:< Option[A1], ev2: Option[B1] <:< B): POptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A <:< A1, evB: B1 <:< B): POptional[S, T, A1, B1] =
    evB.substituteContra[POptional[S, T, A1, -*]](evA.substituteCo[POptional[S, T, +*, B]](this))

  /** compose a [[POptional]] with a [[POptional]] */
  def andThen[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    new POptional[S, T, C, D] {
      def getOrModify(s: S): Either[T, C] =
        self
          .getOrModify(s)
          .flatMap(a => other.getOrModify(a).bimap(self.replace(_)(s), identity))

      override def replace(d: D): S => T =
        self.modify(other.replace(d))

      def getOption(s: S): Option[C] =
        self.getOption(s) flatMap other.getOption

      def modifyA[F[_]: Applicative](f: C => F[D])(s: S): F[T] =
        self.modifyA(other.modifyA(f))(s)

      override def modify(f: C => D): S => T =
        self.modify(other.modify(f))
    }

  /** compose a [[POptional]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    andThen(other)

  /** Compose with a function lifted into a Getter */
  override def to[C](f: A => C): Fold[S, C] = andThen(Getter(f))

  /** compose a [[POptional]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
    andThen(other)

  /** compose a [[POptional]] with a [[PSetter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** compose a [[POptional]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    andThen(other)

  /** compose a [[POptional]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** compose a [[POptional]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** compose a [[POptional]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** compose a [[POptional]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def composeIso[C, D](other: PIso[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** *****************************************
    */
  /** Experimental aliases of compose methods */
  /** *****************************************
    */
  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def ^|->>[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def ^|-?[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def ^<-?[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def ^|->[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  override def ^<->[C, D](other: PIso[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** ******************************************************************
    */
  /** Transformation methods to view a [[POptional]] as another Optics */
  /** ******************************************************************
    */

  /** view a [[POptional]] as a [[PTraversal]] */
  def asTraversal: PTraversal[S, T, A, B] = this
}

object POptional extends OptionalInstances {
  @deprecated("use PIso.id", since = "3.0.0-M2")
  def id[S, T]: POptional[S, T, S, T] =
    PIso.id[S, T]

  def codiagonal[S, T]: POptional[Either[S, S], Either[T, T], S, T] =
    POptional[Either[S, S], Either[T, T], S, T](
      _.fold(Either.right, Either.right)
    )(t => _.bimap(_ => t, _ => t))

  /** create a [[POptional]] using the canonical functions: getOrModify and replace */
  def apply[S, T, A, B](_getOrModify: S => Either[T, A])(_set: B => S => T): POptional[S, T, A, B] =
    new POptional[S, T, A, B] {
      def getOrModify(s: S): Either[T, A] =
        _getOrModify(s)

      override def replace(b: B): S => T =
        _set(b)

      def getOption(s: S): Option[A] =
        _getOrModify(s).toOption

      def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        _getOrModify(s).fold(
          t => Applicative[F].pure(t),
          a => Applicative[F].map(f(a))(_set(_)(s))
        )

      override def modify(f: A => B): S => T =
        s => _getOrModify(s).fold(identity, a => _set(f(a))(s))
    }

  implicit def optionalSyntax[S, A](self: Optional[S, A]): OptionalSyntax[S, A] =
    new OptionalSyntax(self)
}

object Optional {
  @deprecated("use Iso.id", since = "3.0.0-M2")
  def id[A]: Optional[A, A] =
    Iso.id[A]

  def codiagonal[S]: Optional[Either[S, S], S] =
    POptional.codiagonal

  /** [[Optional]] that points to nothing */
  def void[S, A]: Optional[S, A] =
    Optional[S, A](_ => None)(_ => identity)

  /** Select all the elements which satisfies the predicate.
    * {{{
    *   val positiveNumbers = Traversal.fromTraverse[List, Int] composeOptional filter[Int](_ >= 0)
    *
    *   positiveNumbers.getAll(List(1,2,-3,4,-5)) == List(1,2,4)
    *   positiveNumbers.modify(_ * 10)(List(1,2,-3,4,-5)) == List(10,20,-3,40,-5)
    * }}}
    *
    * `filter` can break the fusion property, if `replace` or `modify` do not preserve the predicate.
    * For example, here the first `modify` (`x - 3`) transform the positive number 1 into the
    * negative number -2.
    * {{{
    *   val positiveNumbers = Traversal.fromTraverse[List, Int] composeOptional Optional.filter[Int](_ >= 0)
    *   val list            = List(1, 5, -3)
    *   val firstStep       = positiveNumbers.modify(_ - 3)(list)            // List(-2, 2, -3)
    *   val secondStep      = positiveNumbers.modify(_ * 2)(firstStep)       // List(-2, 4, -3)
    *   val bothSteps       = positiveNumbers.modify(x => (x - 3) * 2)(list) // List(-4, 4, -3)
    *   secondStep != bothSteps
    * }}}
    *
    * @see This method is called `filtered` in Haskell Lens.
    */
  def filter[A](predicate: A => Boolean): Optional[A, A] =
    Optional[A, A](value => if (predicate(value)) Some(value) else None)(newValue =>
      current => if (predicate(current)) newValue else current
    )

  /** alias for [[POptional]] apply restricted to monomorphic update */
  def apply[S, A](_getOption: S => Option[A])(_set: A => S => S): Optional[S, A] =
    new Optional[S, A] {
      def getOrModify(s: S): Either[S, A] =
        _getOption(s).fold[Either[S, A]](Either.left(s))(Either.right)

      override def replace(a: A): S => S =
        _set(a)

      def getOption(s: S): Option[A] =
        _getOption(s)

      def modifyA[F[_]: Applicative](f: A => F[A])(s: S): F[S] =
        _getOption(s).fold(Applicative[F].pure(s))(a => Applicative[F].map(f(a))(_set(_)(s)))

      override def modify(f: A => A): S => S =
        s => _getOption(s).fold(s)(a => _set(f(a))(s))
    }
}

sealed abstract class OptionalInstances {
  implicit val optionalChoice: Choice[Optional] = new Choice[Optional] {
    def choice[A, B, C](f: Optional[A, C], g: Optional[B, C]): Optional[Either[A, B], C] =
      f choice g

    def id[A]: Optional[A, A] =
      Iso.id[A]

    def compose[A, B, C](f: Optional[B, C], g: Optional[A, B]): Optional[A, C] =
      g.andThen(f)
  }
}

/** Extension methods for monomorphic Optional
  */
final case class OptionalSyntax[S, A](private val self: Optional[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): Traversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Optional[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Traversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Optional[S, A1] =
    self.adapt(evOpt, evOpt.flip).andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): Optional[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Optional[S, A1] =
    self.andThen(evIndex.index(i))
}

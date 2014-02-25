package monocle

import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}
import scalaz._
import scalaz.\/-
import scala.{Some, None}


trait Prism[S, T, A, B] extends Traversal[S, T, A, B] { self =>

  def re: Getter[B, T]

  def getOption(from: S): Option[A] = headOption(from)

  def asPrism: Prism[S, T, A, B] = self

  def compose[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = new Prism[S, T, C, D] {
    def re: Getter[D, T] = other.re compose self.re

    def multiLift[F[_] : Applicative](from: S, f: C => F[D]): F[T] = self.multiLift(from, other.multiLift(_, f))
  }

}

object Prism {

  def apply[S, T, A, B](_reverseGet: B => T, seta: S => T \/ A): Prism[S, T, A, B] = new Prism[S, T, A, B]{
    def re: Getter[B, T] = Getter[B, T](_reverseGet)

    def multiLift[F[_] : Applicative](from: S, f: A => F[B]): F[T] =
      seta(from)   // T \/ A
        .map(f)    // T \/ F[B]
        .map(Applicative[F].map(_)(_reverseGet)) // T \/ F[T]
        .leftMap(Applicative[F].point(_))        // F[T] \/ F[T]
        .fold(identity, identity)                // F[T]
  }


  def laws[S: Arbitrary : Equal, A : Arbitrary : Equal](prism: SimplePrism[S, A]) = new Properties("Prism") {
    import scalaz.std.option._

    include(Traversal.laws(prism))

    property("re - getOption") = forAll { value: A =>
      Equal[Option[A]].equal(prism.getOption(prism.re.get(value)), Some(value))
    }

    property("focus is smaller") = forAll { (from: S, newValue: A) =>
      prism.getOption(from).map{ someA =>
        Equal[S].equal(prism.re.get(someA), from)
      }.getOrElse(true)
    }
  }

  def _Some[A, B]: Prism[Option[A], Option[B], A, B] =
    Prism[Option[A], Option[B], A, B](Some.apply, _.map(\/-(_)) getOrElse -\/(None))

  def _None[A]: SimplePrism[Option[A] , Unit] =
    SimplePrism[Option[A] , Unit](_ => None, { opt => if(opt == None) Some(()) else None } )

  def _Left[A, B, C]: Prism[A \/ B, C \/ B, A, C] =
    Prism[A \/ B, C \/ B, A, C](-\/.apply, {
      case -\/(a) => \/-(a)
      case \/-(b) => -\/(\/-(b))
    } )

  def _Right[A, B, C]: Prism[A \/ B, A \/ C, B, C] =
    Prism[A \/ B, A \/ C, B, C](\/-.apply, {
      case -\/(a) => -\/(-\/(a))
      case \/-(b) => \/-(b)
    } )

}

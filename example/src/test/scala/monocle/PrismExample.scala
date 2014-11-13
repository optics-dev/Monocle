package monocle

import org.specs2.scalaz.Spec

import scalaz.Tags.Multiplication
import scalaz._
import scalaz.std.anyVal._


class PrismExample extends Spec {

  sealed trait Expression[T]
  case class Atom[T](value: T) extends Expression[T]
  case class Plus[T](left: Expression[T], right: Expression[T]) extends Expression[T]

  def atom[A, B: Monoid] = PPrism[Expression[A], Expression[B], A, B]{
    case Atom(v)    => \/-(v)
    case Plus(_, _) => -\/(Atom(Monoid[B].zero))
  }(Atom.apply[B])

  val mAtom = atom[Int, Int @@ Multiplication]

  "Prism getMaybe extracts the target (A) of a Prism if it exists" in {
    mAtom.getMaybe(Atom(3))                ==== Maybe.just(3)
    mAtom.getMaybe(Plus(Atom(1), Atom(2))) ==== Maybe.empty
  }

  "Prism getOrModify extracts the target (A) of a Prism if it exists or it modifies the source (T)" in {
    mAtom.getOrModify(Atom(3))                ==== \/-(3)
    mAtom.getOrModify(Plus(Atom(1), Atom(2))) ==== -\/(Atom(Multiplication(1)))
  }

  "Prism reverseGet transforms the modified target (B) in a modified source (T)" in {
    mAtom.reverseGet(Multiplication(1)) ==== Atom(Multiplication(1))
  }


}

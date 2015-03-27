package monocle.law

import monocle.Traversal
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.Id._
import scalaz.syntax.equal._
import scalaz.std.list._
import scalaz.std.option._

object TraversalLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](traversal: Traversal[S, A]) = new Properties("Traversal") {

    /** set does not change the number of targets */
    property("you get what you set") = forAll { (s: S, a: A) =>
      traversal.getAll(traversal.set(a)(s)) === traversal.getAll(s).map(_ => a)
    }

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      traversal.set(a)(traversal.set(a)(s)) === traversal.set(a)(s)
    }

    /** modifyF does not change the number of targets */
    property("modifyF preserves the structure") = forAll { s: S =>
      traversal.modifyF[Id](id.point[A](_))(s) === s
    }

    /** modify does not change the number of targets */
    property("modify preserves the structure") = forAll { s: S =>
      traversal.modify(identity)(s) === s
    }

    property("headMaybe returns the first element of getAll (if getAll is finite)") = forAll { s: S =>
      traversal.headOption(s) === traversal.getAll(s).headOption
    }

  }

}

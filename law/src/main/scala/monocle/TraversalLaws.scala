package monocle

import scalaz.Equal
import scalaz.Id._
import scalaz.std.list._
import scalaz.syntax.equal._
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}

object TraversalLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](traversal: SimpleTraversal[S, A]) = new Properties("Traversal") {
    include(SetterLaws(traversal.asSetter))

    property("multi lift - identity") = forAll { from: S =>
      traversal.multiLift[Id](from, id.point[A](_)) === from
    }

    property("set - get all") = forAll { (from: S, newValue: A) =>
      traversal.getAll(traversal.set(from, newValue)) === traversal.getAll(from).map(_ => newValue)
    }
  }

}

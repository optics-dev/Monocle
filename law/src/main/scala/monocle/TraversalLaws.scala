package monocle

import org.scalacheck.{Properties, Arbitrary}
import scalaz.Equal
import org.scalacheck.Prop._
import scalaz.Id._
import scalaz.std.list._

object TraversalLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](traversal: SimpleTraversal[S, A]) = new Properties("Traversal") {

    import scalaz.syntax.equal._

    include(SetterLaws(traversal))

    property("multi lift - identity") = forAll { from: S =>
      traversal.multiLift[Id](from, id.point[A](_)) === from
    }

    property("set - get all") = forAll { (from: S, newValue: A) =>
      traversal.getAll(traversal.set(from, newValue)) === traversal.getAll(from).map(_ => newValue)
    }
  }

}

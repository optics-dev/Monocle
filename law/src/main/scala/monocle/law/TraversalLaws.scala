package monocle.law

import monocle.SimpleTraversal
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scalaz.Equal
import scalaz.Id._
import scalaz.syntax.equal._

object TraversalLaws {

  def apply[S: Arbitrary: Equal, A: Arbitrary: Equal](traversal: SimpleTraversal[S, A]) = new Properties("Traversal") {
    include(SetterLaws(traversal.asSetter))

    property("modifyF . id == id") = forAll { s: S =>
      traversal.modifyF[Id](id.point[A](_))(s) === s
    }

    property("set - get all") = forAll { (s: S, a: A) =>
      traversal.getAll(traversal.set(a)(s)) === traversal.getAll(s).map(_ => a)
    }
  }

}

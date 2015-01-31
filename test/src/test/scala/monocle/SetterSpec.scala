package monocle

import monocle.std.ilist._
import org.specs2.scalaz.Spec

import scalaz._


class SetterSpec extends Spec {

  def all[A]: Setter[IList[A], A] = PSetter.fromFunctor[IList, A, A]
  def even[A]: Setter[IList[A], A] = function.filterIndex[IList[A], Int, A](_ % 2 == 0).asSetter

  // test implicit resolution of type classes

  "Setter has a Compose instance" in {
    Compose[Setter].compose(all[Int], all[IList[Int]]).set(3)(IList(IList(1,2,3), IList(4))) ==== IList(IList(3,3,3), IList(3))
  }

  "Setter has a Category instance" in {
    Category[Setter].id[Int].modify(_ + 1)(3) ==== 4
  }

  "Setter has a Choice instance" in {
    Choice[Setter].choice(all[Int], even[Int]).modify(_ + 1)(\/-(IList(1,2,3,4))) ==== \/-(IList(2,2,4,4))
  }


}
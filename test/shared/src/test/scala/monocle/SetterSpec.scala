package monocle

import scalaz._

class SetterSpec extends MonocleSuite {

  def all[A]: Setter[IList[A], A] = PSetter.fromFunctor[IList, A, A]
  def even[A]: Setter[IList[A], A] = filterIndex[IList[A], Int, A](_ % 2 == 0).asSetter

  // test implicit resolution of type classes

  test("Setter has a Compose instance") {
    Compose[Setter].compose(all[Int], all[IList[Int]]).set(3)(IList(IList(1,2,3), IList(4))) shouldEqual IList(IList(3,3,3), IList(3))
  }

  test("Setter has a Category instance") {
    Category[Setter].id[Int].modify(_ + 1)(3) shouldEqual 4
  }

  test("Setter has a Choice instance") {
    Choice[Setter].choice(all[Int], even[Int]).modify(_ + 1)(\/-(IList(1,2,3,4))) shouldEqual \/-(IList(2,2,4,4))
  }


}
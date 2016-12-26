package monocle

import scalaz.std.list._
import scalaz.{Category, Choice, Compose, Unzip, \/-}

class SetterSpec extends MonocleSuite {

  def eachL[A]: Setter[List[A], A] = PSetter.fromFunctor[List, A, A]
  def even[A]: Setter[List[A], A] = filterIndex[List[A], Int, A](_ % 2 == 0).asSetter

  def eachLi: Setter[List[Int], Int] = eachL[Int]
  def eachL2[A, B]: Setter[List[(A, B)], (A, B)] = eachL[(A, B)]

  // test implicit resolution of type classes

  test("Setter has a Compose instance") {
    Compose[Setter].compose(eachL[Int], eachL[List[Int]]).set(3)(List(List(1,2,3), List(4))) shouldEqual List(List(3,3,3), List(3))
  }

  test("Setter has a Category instance") {
    Category[Setter].id[Int].modify(_ + 1)(3) shouldEqual 4
  }

  test("Setter has a Choice instance") {
    Choice[Setter].choice(eachL[Int], even[Int]).modify(_ + 1)(\/-(List(1,2,3,4))) shouldEqual \/-(List(2,2,4,4))
  }

  test("Setter has an Unzip instance") {
    val (int, string) = Unzip[Setter[List[(Int, String)], ?]].unzip(eachL2[Int, String])
    int.modify(_ + 1)(List((1, "a"), (2, "b"))) shouldEqual List((2, "a"), (3, "b"))
    string.modify(_ + "!")(List((1, "a"), (2, "b"))) shouldEqual List((1, "a!"), (2, "b!"))
  }


  test("set") {
    eachLi.set(0)(List(1,2,3,4)) shouldEqual List(0,0,0,0)
  }

  test("modify") {
    eachLi.modify(_ + 1)(List(1,2,3,4)) shouldEqual List(2,3,4,5)
  }

}

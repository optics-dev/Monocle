package monocle.focus

import monocle.Focus
import monocle.function.Each.*
import monocle.std.list.*

final class FocusEachTest extends munit.FunSuite {

  test("Direct each on the argument") {
    val eachNumber = Focus[List[Int]](_.each)
    val list       = List(1, 2, 3)
    assertEquals(eachNumber.getAll(list), List(1, 2, 3))
    assertEquals(eachNumber.modify(_ + 1)(list), List(2, 3, 4))
  }

  test("Each on a field") {
    case class School(name: String, students: List[Student])
    case class Student(firstName: String, lastName: String, yearLevel: Int)

    val school = School(
      "Sparkvale Primary School",
      List(
        Student("Arlen", "Appleby", 5),
        Student("Bob", "Bobson", 6),
        Student("Carol", "Cornell", 7)
      )
    )

    val studentNames = Focus[School](_.students.each.firstName)
    val studentYears = Focus[School](_.students.each.yearLevel)

    assertEquals(studentNames.getAll(school), List("Arlen", "Bob", "Carol"))
  }

  test("Focus operator `each` commutes with standalone operator `each`") {
    case class School(name: String, students: List[Student])
    case class Student(firstName: String, lastName: String, yearLevel: Int)

    val school = School(
      "Sparkvale Primary School",
      List(
        Student("Arlen", "Appleby", 5),
        Student("Bob", "Bobson", 6),
        Student("Carol", "Cornell", 7)
      )
    )

    assertEquals(Focus[School](_.students.each).getAll(school), Focus[School](_.students).each.getAll(school))
  }
}

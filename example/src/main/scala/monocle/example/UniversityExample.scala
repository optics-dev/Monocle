package monocle.example

import monocle.macros.GenLens


object UniversityExample {

  case class University(name: String, departments: Map[String, Department])
  case class Department(budget: Int, lecturers: List[Lecturer])
  case class Lecturer(firstName: String, lastName: String, salary: Int)

  val departments = GenLens[University](_.departments)
  val lecturers = GenLens[Department](_.lecturers)
  val salary = GenLens[Lecturer](_.salary)
  val firstName = GenLens[Lecturer](_.firstName)
  val lastName = GenLens[Lecturer](_.lastName)
}

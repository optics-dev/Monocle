package monocle.focus

import monocle.Focus

final class FocusImportTest extends munit.FunSuite {

  case class User(name: String, address: Option[Address])
  case class Address(streetNumber: Int, postcode: String)

  test("import Focus object") {
    import monocle.Focus._
    Focus[User](_.address.some.streetNumber)
  }

  test("import all syntax") {
    import monocle.syntax.all._
    Focus[User](_.address.some.streetNumber)
  }

}
package monocle.focus

import monocle.Focus

final class FocusImportTest extends munit.FunSuite {

  case class User(name: String, address: Option[Address])
  case class Address(streetNumber: Int, postcode: String)

  val user = User("Edith", Some(Address(45, "2120")))

  test("import Focus object") {
    import monocle.Focus.*
    user.focus(_.address.some.streetNumber)
  }

  test("import all syntax") {
    import monocle.syntax.all.*
    user.focus(_.address.some.streetNumber)
  }

}

package monocle

import monocle.std.option.some
import monocle.state.all._

object Main {

  case class User(name: String, email: Option[String])

  val _name = Lens[User, String](_.name)(n => u => u.copy(name = n))
  val _email = Lens[User, Option[String]](_.email)(e => u => u.copy(email = e))

  def main(args: Array[String]): Unit = {

    val user = User("John Doe", Some("foo@example.com"))

    assert(_name.get(user) == "John Doe")
    assert(_name.modify(_.replace(' ', '-'))(user) == user.copy(name = "John-Doe"))
    assert((_email composePrism some).getOption(user) == Some("foo@example.com"))

    val getSetName = for {
      n1 <- _name.extract
      n2 <- _name.assigno(n1)
    } yield n2

    val setGetName = for {
      user <- _name.assigno("Eric")
      name <- _name.extract
    } yield name

    val setSetName = for {
      n1 <- _name.assigno("Brian")
      n2 <- _name.assigno("Luke")
    } yield n2

    assert(getSetName.exec(user) == user)
    assert(setGetName.eval(user) == "Eric")
    assert(setSetName.exec(user) == _name.set("Luke")(user))
  }
}

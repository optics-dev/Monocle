package monocle.focus

import monocle.Focus.*
import monocle.Focus
import monocle.Iso

final class NamedTupleFocusTest extends munit.FunSuite {

  test("Applied focus returning an Optional in nested named tuples") {
    // works when type of address is widened from Some to Option
    // the macro cannot find a matching overload otherwise - note that this also happens in a normal .focus invocation on case classes when a type is described as Some instead of Option
    val elise = (name = "Elise", address = Option((streetNumber = 12, postcode = "high street")))

    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, (name = "Elise", address = Some((streetNumber = 50, postcode = "high street"))))
  }

  test("Applied focus returning an Optional with a named tuple inside a case class") {
    case class User(name: String, address: Option[Address])
    type Address = (streetNumber: Int, postcode: String)

    val elise = User("Elise", Some((streetNumber = 12, postcode = "high street")))

    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, User("Elise", Some((50, "high street"))))
  }

  test("Applied focus returning an Optional with a case class inside a named tuple") {
    case class Address(streetNumber: Int, postcode: String)

    val elise = (name = "Elise", address = Option(Address(12, "high street")))

    val streetNumber = elise.focus(_.address.some.streetNumber).getOption
    val newElise     = elise.focus(_.address.some.streetNumber).replace(50)

    assertEquals(streetNumber, Some(12))
    assertEquals(newElise, (name = "Elise", address = Some(Address(50, "high street"))))
  }

  test("Applied focus returning an Optional with NamedTuple.From") {
    case class User[A](name: String, address: A)
    case class Address(streetNumber: Int, postcode: String)

    type Bob = NamedTuple.From[User[Option[NamedTuple.From[Address]]]]

    val bob: Bob = (
      name = "Bob",
      address = Option(streetNumber = 5, postcode = "Bob St")
    )

    val streetNumber = bob.focus(_.address.some.streetNumber).getOption
    val newBob       = bob.focus(_.address.some.streetNumber).replace(77)

    assertEquals(streetNumber, Some(5))
    assertEquals(newBob, (
      name = "Bob",
      address = Option(streetNumber = 77, postcode = "Bob St")
    ))
  }

  test("Applied focus returning a Lens in nested named tuples") {
    val bob = (
      name = "Bob",
      address = (streetNumber = 5, postcode = "Bob St")
    )

    val streetNumber = bob.focus(_.address.streetNumber).get
    val newBob       = bob.focus(_.address.streetNumber).replace(77)

    assertEquals(streetNumber, 5)
    assertEquals(newBob, (name = "Bob", address = (streetNumber = 77, postcode = "Bob St")))
  }

  test("Applied focus returning a Lens with a named tuple inside a case class") {
    case class User(name: String, address: Address)
    type Address = (streetNumber: Int, postcode: String)

    val bob = User("Bob", (streetNumber = 5, postcode = "Bob St"))

    val streetNumber = bob.focus(_.address.streetNumber).get
    val newBob       = bob.focus(_.address.streetNumber).replace(77)

    assertEquals(streetNumber, 5)
    assertEquals(newBob, User("Bob", (streetNumber = 77, postcode = "Bob St")))
  }

   test("Applied focus returning a Lens with a case class inside a named tuple") {
     case class Address(streetNumber: Int, postcode: String)
     type User = (name: String, address: Address)
     val bob: User = (
       name = "Bob",
       address = Address(5, "Bob St")
     )

     val streetNumber = bob.focus(_.address.streetNumber).get
     val newBob       = bob.focus(_.address.streetNumber).replace(77)

     assertEquals(streetNumber, 5)
     assertEquals(newBob, (name = "Bob", address = Address(77, "Bob St")))
   }

  test("Applied focus returning a Lens with NamedTuple.From") {
    case class User[A](name: String, address: A)
    case class Address(streetNumber: Int, postcode: String)

    type Bob = NamedTuple.From[User[NamedTuple.From[Address]]]

    val bob: Bob = (
      name = "Bob",
      address = (streetNumber = 5, postcode = "Bob St")
    )

    val streetNumber = bob.focus(_.address.streetNumber).get
    val newBob       = bob.focus(_.address.streetNumber).replace(77)

    assertEquals(streetNumber, 5)
    assertEquals(newBob, (
      name = "Bob",
      address = (streetNumber = 77, postcode = "Bob St")
    ))

  }

  test("Each on a named tuple field") {
    type School = (name: String, students: List[Student])
    type Student = (firstName: String, lastName: String, yearLevel: Int)

    val school: School = (
      name = "Sparkvale Primary School",
      students = List(
        (firstName = "Arlen", lastName = "Appleby",  yearLevel = 5),
        (firstName = "Bob", lastName = "Bobson", yearLevel = 6),
        (firstName = "Carol", lastName =  "Cornell", yearLevel = 7)
      )
    )

    val studentNames = Focus[School](_.students.each.firstName)

    assertEquals(studentNames.getAll(school), List("Arlen", "Bob", "Carol"))
  }

  type Fub = (bab: Int)
  def Fub(bab: Int): Fub = (bab = bab)

  type Bar = (fub: Fub)
  def Bar(fub: Fub): Bar = (fub = fub)

  type Foo = (bar: Option[Bar])
  def Foo(bar: Option[Bar]) = (bar = bar)

  type Qux = (foo: Either[String, Foo], moo: Map[Int, Fub])
  def Qux(foo: Either[String, Foo], moo: Map[Int, Fub]) = (foo = foo, moo = moo)

  type Animal = (name: String)
  def Animal(name: String): Animal = (name = name)

  type Owner = (pet: Animal)
  def Owner(pet: Animal): Owner = (pet = pet) 

  type Shop = (owner: Owner)
  def Shop(owner: Owner): Shop = (owner = owner)

  type Box[A] = (a: A)
  def Box[A](a: A): Box[A] = (a = a)

  type MultiBox[A, B] = (a: A, b: B)
  def MultiBox[A, B](a: A, b: B): MultiBox[A, B] = (a = a, b = b)

  type HigherBox[F[_], A] = (fa: F[A])
  def HigherBox[F[_], A](fa: F[A]): HigherBox[F, A] = (fa = fa)

  type UnionBox[A, B]= (aOrB: A | B)
  def UnionBox[A, B](aOrB: A | B):UnionBox[A, B] = (aOrB = aOrB)

  type ConstraintBox[A <: AnyVal] = (a: A)
  def ConstraintBox[A <: AnyVal](a: A): ConstraintBox[A] = (a = a)

  test("Single field access") {
    assertEquals(
      Focus[Animal](_.name).get(Animal("Bob")),
      "Bob"
    )
  }

  test("Nested field access") {
    assertEquals(
      Focus[Shop](_.owner.pet.name).get(Shop(Owner(Animal("Fred")))),
      "Fred"
    )
  }

  test("Type parameter field access") {
    assertEquals(
      Focus[Box[String]](_.a).get(Box("Hello")),
      "Hello"
    )
  }

  test("Type parameter set field") {
    assertEquals(
      Focus[Box[Int]](_.a).replace(111)(Box(222)),
      Box(111)
    )
  }

  test("Nested type parameter set field") {
    assertEquals(
      Focus[Box[Box[String]]](_.a.a).replace("hello")(Box(Box("ok"))),
      Box(Box("hello"))
    )
  }

  test("Multiple type parameters get field") {
    assertEquals(
      Focus[MultiBox[Int, Boolean]](_.b).get(MultiBox(222, true)),
      true
    )
  }

  test("Multiple type parameters set field") {
    assertEquals(
      Focus[MultiBox[String, Int]](_.a).replace("abc")(MultiBox("whatevs", 222)),
      MultiBox("abc", 222)
    )
  }

  test("Higher kinded type parameter get field") {
    assertEquals(
      Focus[HigherBox[Option, Int]](_.fa).get(HigherBox(Some(23))),
      Some(23)
    )
  }

  test("Single field should be an Iso") {
    val iso: Iso[Animal, String] = Focus[Animal](_.name)
    assertEquals(iso.get(Animal("Bob")), "Bob")
    assertEquals(iso.reverseGet("Bob"), Animal("Bob"))
  }

  test("Type alias for parameterised type access") {
    type CC[T] = (t: T, i: Int)
    type CCInt = CC[Int]
    val cc: CCInt = (t = 2, i = 3)

    assertEquals(Focus[CCInt](_.i).get(cc), 3)
    assertEquals(Focus[CCInt](_.t).get(cc), 2)
  }


  
}

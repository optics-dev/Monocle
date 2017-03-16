package other

import monocle.{Iso, MonocleSuite}
import monocle.law.discipline.{IsoTests, LensTests, PrismTests}
import monocle.macros.{GenIso, GenLens, GenPrism}
import org.scalacheck.Arbitrary.{arbOption => _, _}
import org.scalacheck.{Cogen, Arbitrary, Gen}

import cats.{Eq => Equal}

class MacroOutSideMonocleSpec extends MonocleSuite {

  case class Example(i: Int)
  case class Example2(l: Long, s: String)
  case object ExampleObject
  case class EmptyCase()
  case class EmptyCaseType[A]()
  case class ExampleType[A](as: Option[A])
  case class Example2Type[A](a: A, as: Option[A])

  sealed trait Foo
  case class Bar1(s: String) extends Foo
  case class Bar2(i: Int) extends Foo

  implicit val exampleArb: Arbitrary[Example] = Arbitrary(arbitrary[Int].map(Example.apply))
  implicit val example2Arb: Arbitrary[Example2] = Arbitrary(for {l <- arbitrary[Long]; s <- arbitrary[String]} yield Example2(l, s))
  implicit val exampleObjArb: Arbitrary[ExampleObject.type] = Arbitrary(Gen.const(ExampleObject))
  implicit val emptyCaseArb: Arbitrary[EmptyCase] = Arbitrary(Gen.const(EmptyCase()))
  implicit def emptyCaseTypeArb[A]: Arbitrary[EmptyCaseType[A]] = Arbitrary(Gen.const(EmptyCaseType()))
  implicit val bar1Arb: Arbitrary[Bar1] = Arbitrary(arbitrary[String].map(Bar1.apply))
  implicit val bar2Arb: Arbitrary[Bar2] = Arbitrary(arbitrary[Int].map(Bar2.apply))
  implicit val fooArb: Arbitrary[Foo] = Arbitrary(Gen.oneOf(arbitrary[Bar1], arbitrary[Bar2]))
  implicit val bar1CoGen: Cogen[Bar1] = Cogen[String].contramap[Bar1](_.s)
  implicit def exampleTypeArb[A: Arbitrary]: Arbitrary[ExampleType[A]] = Arbitrary(Arbitrary.arbOption[A].arbitrary map ExampleType.apply)
  implicit def example2TypeArb[A: Arbitrary]: Arbitrary[Example2Type[A]] =
    Arbitrary(for {x <- arbitrary[A]; y <- Arbitrary.arbOption[A].arbitrary} yield Example2Type(x, y))

  implicit val exampleEq: Equal[Example] = Equal.fromUniversalEquals[Example]
  implicit val example2Eq: Equal[Example2] = Equal.fromUniversalEquals[Example2]
  implicit def exampleTypeEq[A](implicit as: Equal[Option[A]]): Equal[ExampleType[A]] = as.on(_.as)
  implicit def example2TypeEq[A](implicit a: Equal[A], as: Equal[Option[A]]): Equal[Example2Type[A]] = Equal.instance((x, y) => a.eqv(x.a, y.a) && as.eqv(x.as, y.as))
  implicit val exampleObjEq: Equal[ExampleObject.type] = Equal.fromUniversalEquals[ExampleObject.type]
  implicit val emptyCaseEq: Equal[EmptyCase] = Equal.fromUniversalEquals[EmptyCase]
  implicit def emptyCaseTypeEq[A]: Equal[EmptyCaseType[A]] = Equal.fromUniversalEquals[EmptyCaseType[A]]
  implicit val bar1Eq: Equal[Bar1] = Equal.fromUniversalEquals[Bar1]
  implicit val fooEq: Equal[Foo] = Equal.fromUniversalEquals[Foo]

  checkAll("GenIso"                                       , IsoTests(GenIso[Example, Int]))
  checkAll("GenIso.unit object"                           , IsoTests(GenIso.unit[ExampleObject.type]))
  checkAll("GenIso.unit empty case class"                 , IsoTests(GenIso.unit[EmptyCase]))
  checkAll("GenIso.unit empty case class with type param" , IsoTests(GenIso.unit[EmptyCaseType[Int]]))
  checkAll("GenLens"                                      , LensTests(GenLens[Example](_.i)))
  checkAll("GenPrism"                                     , PrismTests(GenPrism[Foo, Bar1]))


  def testGenIsoFields[S: Arbitrary : Equal, A: Arbitrary : Equal : Cogen](name: String, iso: Iso[S, A]) =
    new FieldsTester(name, iso)

  class FieldsTester[S: Arbitrary : Equal, A: Arbitrary : Equal: Cogen](name: String, iso: Iso[S, A]) {
    def expect[Expect](implicit ev: A =:= Expect) =
      checkAll("GenIso.fields " + name, IsoTests(iso))
  }

  testGenIsoFields("empty case class", GenIso.fields[EmptyCase]).expect[Unit]
  testGenIsoFields("case class 1",     GenIso.fields[Example]  ).expect[Int]
  testGenIsoFields("case class 2",     GenIso.fields[Example2] ).expect[(Long, String)]

  testGenIsoFields("empty case class with type param", GenIso.fields[EmptyCaseType[Int]]).expect[Unit]
  testGenIsoFields("case class 1 with type param",     GenIso.fields[ExampleType[Int]]  ).expect[Option[Int]]
  testGenIsoFields("case class 2 with type param",     GenIso.fields[Example2Type[Int]] ).expect[(Int, Option[Int])]
}

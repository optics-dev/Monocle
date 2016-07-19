package monocle.refined

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import monocle.MonocleSuite
import monocle.law.discipline.function.AtTests
import org.scalacheck.{Arbitrary, Gen}

class BitsSpec extends MonocleSuite {
  implicit val byteBitsArb: Arbitrary[ByteBits] = Arbitrary(
    Gen.choose(0, 7).map(Refined.unsafeApply)
  )
  implicit val charBitsArb: Arbitrary[CharBits] = Arbitrary(
    Gen.choose(0, 15).map(Refined.unsafeApply)
  )
  implicit val intBitsArb: Arbitrary[IntBits] = Arbitrary(
    Gen.choose(0, 31).map(Refined.unsafeApply)
  )
  implicit val longBitsArb: Arbitrary[LongBits] = Arbitrary(
    Gen.choose(0, 63).map(Refined.unsafeApply)
  )

  checkAll("Byte at bit", AtTests[Byte, ZeroTo[W.`7`.T], Boolean])
  checkAll("Char at bit", AtTests[Char, ZeroTo[W.`15`.T], Boolean])
  checkAll("Int at bit", AtTests[Int, ZeroTo[W.`31`.T], Boolean])
  checkAll("Long at bit", AtTests[Long, ZeroTo[W.`63`.T], Boolean])
}

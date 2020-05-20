package monocle.function

import monocle.MonocleSuite
import monocle.law.discipline.LensTests

class FieldsSpec extends MonocleSuite {
  implicit val rawField1: Field1[Raw, Boolean] = Field1.fromIso(Raw.toTuple)
  implicit val rawField2: Field2[Raw, Char]    = Field2.fromIso(Raw.toTuple)
  implicit val rawField3: Field3[Raw, Int]     = Field3.fromIso(Raw.toTuple)
  implicit val rawField4: Field4[Raw, Long]    = Field4.fromIso(Raw.toTuple)
  implicit val rawField5: Field5[Raw, Float]   = Field5.fromIso(Raw.toTuple)
  implicit val rawField6: Field6[Raw, Double]  = Field6.fromIso(Raw.toTuple)

  checkAll("Field1 fromIso", LensTests(rawField1.first))
  checkAll("Field2 fromIso", LensTests(rawField2.second))
  checkAll("Field3 fromIso", LensTests(rawField3.third))
  checkAll("Field4 fromIso", LensTests(rawField4.fourth))
  checkAll("Field5 fromIso", LensTests(rawField5.fifth))
  checkAll("Field6 fromIso", LensTests(rawField6.sixth))
}

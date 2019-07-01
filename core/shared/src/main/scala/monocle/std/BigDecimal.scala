package monocle.std

import monocle._

object bigdecimal extends BigDecimalOptics

trait BigDecimalOptics {
  val bigDecimalToLong: Prism[BigDecimal, Long]  =
    Prism[BigDecimal, Long](bi => if(bi.isValidLong) Some(bi.toLongExact) else None)(BigDecimal(_))

  val bigDecimalToInt: Prism[BigDecimal, Int] = bigDecimalToLong composePrism long.longToInt

  // bigDecimalToDouble cannot be a Prism: some doubles loose precision when converted to a BigDecimal:
  // new java.math.BigDecimal(-2.147483649E-1634, MathContext.UNLIMITED) => java.math.BigDecimal = 0
  // Also, only a subset of the BigDecimal can be represented as a Double without loosing precision (see BigDecimal.isExactDouble)
  // Besides, MathContext must be passed when creating the Optic
}

package monocle

sealed trait Arities
final case class Nullary()                                                   extends Arities
final case class Unary(i: Int)                                               extends Arities
final case class Binary(s: String, i: Int)                                   extends Arities
final case class Quintary(c: Char, b: Boolean, s: String, i: Int, f: Double) extends Arities

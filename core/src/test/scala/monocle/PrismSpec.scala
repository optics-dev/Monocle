package monocle

import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._


class PrismSpec extends Spec {

  val intToChar: SimplePrism[Int, Char] =
    SimplePrism[Int, Char](_.toInt, { n: Int => if(n > Char.MaxValue || n < Char.MinValue) None else Some(n.toChar)} )

  checkAll(Prism.laws(intToChar))

}
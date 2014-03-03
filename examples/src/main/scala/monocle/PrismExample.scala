package monocle

import monocle.std.option._
import monocle.thirdparty.scalazEither
import scalazEither._
import scalaz.{ \/-, -\/ }

object PrismExample extends App {

  val intToChar: SimplePrism[Int, Char] =
    SimplePrism[Int, Char](_.toInt, { n: Int => if (n > Char.MaxValue || n < Char.MinValue) None else Some(n.toChar) })

  println(intToChar.getOption(97)) // Some('a')
  println(intToChar.getOption(65537)) // None

  println(intToChar.re.get('a')) // 97

  println(intToChar.set(97, 'z')) // 122

  println(_Some.getOption(Some(1))) // Some(1)
  println(_Some.set(None, 2)) // None
  println(_Some.set(Some(1), 'a')) // Some('a')
  println(_Some.modify(Some(1), { n: Int => n + 2.0 })) // Some(3.0)

  println(_None.getOption(Some(1))) // None
  println(_None.getOption(None)) // Some(())

  println(_Left.getOption(-\/(1))) // Some(1)
  println(_Left.getOption(\/-(1))) // None

  println(_Right.getOption(-\/(1))) // None
  println(_Right.getOption(\/-(1))) // Some(1)

}

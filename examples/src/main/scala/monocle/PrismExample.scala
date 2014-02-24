package monocle


object PrismExample extends App {

  val intToChar: SimplePrism[Int, Char] =
    SimplePrism[Int, Char](_.toInt, { n: Int => if(n > Char.MaxValue || n < Char.MinValue) None else Some(n.toChar)} )

  println(intToChar.getOption(97))     // Some('a')
  println(intToChar.getOption(65537))  // None

  println(intToChar.re.get('a'))       // 97

  println(intToChar.set(97, 'z'))      // 122

}

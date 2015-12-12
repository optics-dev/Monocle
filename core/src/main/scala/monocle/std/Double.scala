package monocle.std

import monocle.Prism

object double extends DoubleOptics

trait DoubleOptics {

  val doubleToInt: Prism[Double, Int] =
    Prism[Double, Int](d => if(d.isValidInt) Some(d.toInt) else None)(_.toDouble)

  val doubleToFloat: Prism[Double, Float] =
    Prism[Double, Float](d => if(d.isValidInt) Some(d.toInt) else None)(_.toDouble)

}

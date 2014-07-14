package monocle.std

import monocle.SimplePrism
import monocle.function.SafeCast


object double extends DoubleInstances

trait DoubleInstances {

  implicit val doubleToInt: SafeCast[Double, Int] = new SafeCast[Double, Int] {
    def safeCast = SimplePrism[Double, Int](_.toDouble, d => if(d.isValidInt) Some(d.toInt) else None)
  }

}

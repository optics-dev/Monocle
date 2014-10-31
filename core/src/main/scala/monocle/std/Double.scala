package monocle.std

import monocle.Prism
import monocle.function.SafeCast

import scalaz.Maybe

object double extends DoubleInstances

trait DoubleInstances {

  implicit val doubleToInt: SafeCast[Double, Int] = new SafeCast[Double, Int] {
    def safeCast = Prism[Double, Int](d => if(d.isValidInt) Maybe.just(d.toInt) else Maybe.empty)(_.toDouble)
  }

}

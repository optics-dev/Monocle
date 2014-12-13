package monocle.std

import monocle.Prism

import scalaz.Maybe

object double extends DoubleInstances

trait DoubleInstances {

  val doubleToInt: Prism[Double, Int] =
    Prism[Double, Int](d => if(d.isValidInt) Maybe.just(d.toInt) else Maybe.empty)(_.toDouble)

}

package monocle.std

import monocle.Prism

object double extends DoubleInstances

trait DoubleInstances {

  val doubleToInt: Prism[Double, Int] =
    Prism[Double, Int](d => if(d.isValidInt) Some(d.toInt) else None)(_.toDouble)

}

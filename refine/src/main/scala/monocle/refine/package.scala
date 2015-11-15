package monocle

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval

package object refine {

  type ZeroTo[T] = Int Refined Interval[W.`0`.T, T]

}

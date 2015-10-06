package monocle

import eu.timepit.refined.{Refined, W}
import eu.timepit.refined.numeric._

package object date {

  type Minute = Int Refined Interval[W.`0`.T, W.`59`.T]
  type Hour   = Int Refined Interval[W.`0`.T, W.`23`.T]

}

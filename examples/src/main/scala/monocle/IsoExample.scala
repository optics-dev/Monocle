package monocle

import monocle.ExampleInstances.Location

object IsoExample extends App {
  import Location._

  val locationPairIso = SimpleIso[Location, (Double, Double)](
    { l => (l._latitude, l._longitude) },
    { case (lat, long) => Location(lat, long) })

  val l = Location(3.0, 5.0)
  val p = (3.0, 5.0)

  println(locationPairIso.set(l, (4.0, 6.0))) // Location(4.0, 6.0)

  println(locationPairIso.reverse compose latitude modify (p, _ + 1)) // (4.0, 5.0)

}

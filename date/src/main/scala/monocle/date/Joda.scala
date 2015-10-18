package monocle.date

import eu.timepit.refined.api.Refined
import monocle.Lens
import org.joda.time.DateTime

object joda {

  val minute = Lens[DateTime, Minute](d => Refined.unsafeApply(d.getMinuteOfHour))(m => d => d.withMinuteOfHour(m.get))
  val hour   = Lens[DateTime, Hour](d => Refined.unsafeApply(d.getHourOfDay))(h => d => d.withHourOfDay(h.get))

}
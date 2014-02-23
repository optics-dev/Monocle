package monocle

import org.specs2.scalaz.Spec
import scalaz.std.AllInstances._


class IsoSpec extends Spec {

  val booleanToOptionOfUnitIso = SimpleIso[Boolean, Option[Unit]](if(_) Some() else None, _.map(_ => true).getOrElse(false))

  checkAll(Iso.laws(booleanToOptionOfUnitIso))

}

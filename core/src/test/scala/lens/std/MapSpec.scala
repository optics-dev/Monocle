package lens.std

import org.scalacheck.Arbitrary._
import org.scalatest.Matchers._
import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks


class MapSpec extends PropSpec with PropertyChecks {

  property("get") {
    forAll { (map: Map[Int, String], key: Int) =>
      Map.at(key).get(map) should be (map.get(key))
    }
  }

  property("set") {
    forAll { (map: Map[Int, String], key: Int, value: String) =>
      val lens = Map.at[Int, String](key)
      lens.get(lens.set(map, Some(value))) should be (Some(value))
    }
  }

  property("delete") {
    forAll { (map: Map[Int, String], key: Int) =>
      val lens = Map.at[Int, String](key)
      lens.get(lens.set(map, None)) should be (None)
    }
  }

}

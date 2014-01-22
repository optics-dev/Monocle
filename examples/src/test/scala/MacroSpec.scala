import lens.Macro
import org.scalatest.{Matchers, WordSpec}
import scala.language.higherKinds

class MacroSpec extends WordSpec with Matchers {

  case class Example(s: String, n: Int)
  val example = Example("plop", 2)

  "A Lens Macro" should {

    "create a setter" in {
      val setter = Macro.mkSetter[Example, String]("s")
      setter(example, "updated") should be (Example("updated", 2))
    }

    "create a getter" in {
      val getter = Macro.mkGetter[Example, String]("s")
      getter(example) should be ("plop")
    }

    "generate a Lens" in {
      val StringLens = Macro.mkLens[Example, String]("s")

      StringLens.get(example) should be ("plop")
    }

  }



}

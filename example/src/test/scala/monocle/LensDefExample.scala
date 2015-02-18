package monocle

import monocle.macros.{GenLens, Lenses}
import monocle.syntax._
import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped

class LensDefExample extends Spec {
  
  @Lenses // this annotation generate lenses in the companion object of Foo
  case class Foo[A,B](q: Map[(A,B),Double], default: Double)

  object CoreSimpleLens {
    def _q[A,B] = Lens((_: Foo[A,B]).q)(q => f => f.copy(q = q))
    def _default[A,B]  = Lens((_: Foo[A,B]).default)(d => f => f.copy(default = d))
  }

  object LenserMacro {
    def genLens[A,B] = GenLens[Foo[A,B]]

    def q[A,B] = genLens[A,B](_.q)
    def default[A,B]  = genLens[A,B](_.default)
  }

  val candyTrade = Foo[Int,Symbol](Map[(Int,Symbol),Double]((0,'Buy) -> -3.0, (12,'Sell) -> 7), 0.0)
  
  "Lens get extract an A from an S" in {
    (candyTrade applyLens CoreSimpleLens._default get) ==== 0.0
    (candyTrade applyLens LenserMacro.default get)     ==== 0.0
    (candyTrade applyLens Foo.default get)             ==== 0.0
  }


  "Lens modify an A in S" in {
    val changedTrade = Foo[Int,Symbol](Map((0,'Buy) -> -2.0, (12,'Sell) -> 7), 0.0)
    import Foo._
    changedTrade ==== changedTrade
    q.modify((_: Map[(Int,Symbol),Double]).updated((0,'Buy), -2.0))(candyTrade) ==== changedTrade
  }
}

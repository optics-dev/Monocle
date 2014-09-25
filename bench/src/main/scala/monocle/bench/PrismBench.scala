package monocle.bench

import monocle.SimplePrism
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class PrismBench {


  sealed trait ADT
  case class I(i: Int)    extends ADT
  case class S(s: String) extends ADT

  def getIMaybe(adt: ADT): Maybe[Int]    = adt match { case I(i) => Maybe.just(i); case _ => Maybe.empty }
  def getSMaybe(adt: ADT): Maybe[String] = adt match { case S(s) => Maybe.just(s); case _ => Maybe.empty }

  def mkI(i: Int)   : ADT = I(i)
  def mkS(s: String): ADT = S(s)

  val _i = SimplePrism[ADT, Int]   (getIMaybe, mkI)
  val _s = SimplePrism[ADT, String](getSMaybe, mkS)

  @Benchmark def directSuccessGetOption() = getIMaybe(mkI(5))     == Maybe.just(5)
  @Benchmark def directFailureGetOption() = getIMaybe(mkS("Yop")) == Maybe.empty[Int]

  @Benchmark def prismSuccessGetOption() = _i.getMaybe(mkI(5))     == Maybe.just(5)
  @Benchmark def prismFailureGetOption() = _i.getMaybe(mkS("Yop")) == Maybe.empty[Int]

}

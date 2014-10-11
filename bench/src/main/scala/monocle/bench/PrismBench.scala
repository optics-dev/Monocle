package monocle.bench

import monocle.SimplePrism
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class PrismBench {

  sealed trait ADT
  case class I(i: Int)    extends ADT
  case class S(s: String) extends ADT
  case class R(r: ADT)    extends ADT

  def getIMaybe(adt: ADT): Maybe[Int]    = adt match { case I(i) => Maybe.just(i); case _ => Maybe.empty }
  def getSMaybe(adt: ADT): Maybe[String] = adt match { case S(s) => Maybe.just(s); case _ => Maybe.empty }
  def getRMaybe(adt: ADT): Maybe[ADT]    = adt match { case R(r) => Maybe.just(r); case _ => Maybe.empty }

  def mkI(i: Int)   : ADT = I(i)
  def mkS(s: String): ADT = S(s)
  def mkR(r: ADT)   : ADT = R(r)

  val _i = SimplePrism(getIMaybe)(mkI)
  val _s = SimplePrism(getSMaybe)(mkS)
  val _r = SimplePrism(getRMaybe)(mkR)

  @Benchmark def directSuccessGetOption() = getIMaybe(mkI(5))     == Maybe.just(5)
  @Benchmark def directFailureGetOption() = getIMaybe(mkS("Yop")) == Maybe.empty[Int]

  @Benchmark def prismSuccessGetOption() = _i.getMaybe(mkI(5))     == Maybe.just(5)
  @Benchmark def prismFailureGetOption() = _i.getMaybe(mkS("Yop")) == Maybe.empty[Int]

  @Benchmark def nestedDirectGetOption() = (for {
    r2 <- getRMaybe(mkR(mkR(mkI(5))))
    r1 <- getRMaybe(r2)
    i  <- getIMaybe(r1)
  } yield i) == Maybe.just(5)

  @Benchmark def nestedPrismGetOption() = (_r composePrism _r composePrism _i).getMaybe(mkR(mkR(mkI(5)))) == Maybe.just(5)

}

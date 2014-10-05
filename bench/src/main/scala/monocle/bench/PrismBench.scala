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

  def getIOption(adt: ADT): Option[Int]    = adt match { case I(i) => Option(i); case _ => Option.empty }
  def getSOption(adt: ADT): Option[String] = adt match { case S(s) => Option(s); case _ => Option.empty }
  def getROption(adt: ADT): Option[ADT]    = adt match { case R(r) => Option(r); case _ => Option.empty }

  def mkI(i: Int)   : ADT = I(i)
  def mkS(s: String): ADT = S(s)
  def mkR(r: ADT)   : ADT = R(r)

  val _i = SimplePrism[ADT, Int]   (mkI, getIOption)
  val _s = SimplePrism[ADT, String](mkS, getSOption)
  val _r = SimplePrism[ADT, ADT]   (mkR, getROption)

  @Benchmark def directSuccessGetOption() = getIOption(mkI(5))     == Option(5)
  @Benchmark def directFailureGetOption() = getIOption(mkS("Yop")) == Option.empty[Int]

  @Benchmark def prismSuccessGetOption() = _i.getOption(mkI(5))     == Option(5)
  @Benchmark def prismFailureGetOption() = _i.getOption(mkS("Yop")) == Option.empty[Int]

  @Benchmark def nestedDirectGetOption() = (for {
    r2 <- getROption(mkR(mkR(mkI(5))))
    r1 <- getROption(r2)
    i  <- getIOption(r1)
  } yield i) == Option(5)

  @Benchmark def nestedPrismGetOption() = (_r composePrism _r composePrism _i).getOption(mkR(mkR(mkI(5)))) == Option(5)

}

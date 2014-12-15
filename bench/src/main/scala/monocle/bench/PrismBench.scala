package monocle.bench

import monocle.Prism
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

  val _i = Prism(getIMaybe)(mkI)
  val _s = Prism(getSMaybe)(mkS)
  val _r = Prism(getRMaybe)(mkR)

  val intADT      = mkI(5)
  val stringADT   = mkS("Yop")
  val nested3Value = mkR(mkR(mkR(mkI(5))))
  val nested6Value = mkR(mkR(mkR(mkR(mkR(mkR(mkI(5)))))))

  @Benchmark def stdSuccessGetMaybe() = getIMaybe(intADT)
  @Benchmark def prismSuccessGetMaybe() = _i.getMaybe(intADT)
  
  @Benchmark def stdFailureGetMaybe() = getIMaybe(stringADT)
  @Benchmark def prismFailureGetMaybe() = _i.getMaybe(stringADT)

  @Benchmark def stdReverseGet()  = mkI(5)
  @Benchmark def prismReverseGet() = _i.reverseGet(5)

  @Benchmark def stdNested3GetMaybe() = for {
    r3 <- getRMaybe(nested3Value)
    r2 <- getRMaybe(r3)
    r1 <- getRMaybe(r2)
    i  <- getIMaybe(r1)
  } yield i
  @Benchmark def prismNested3GetMaybe() = (_r composePrism _r composePrism _r composePrism _i).getMaybe(nested3Value)

  @Benchmark def stdNested6GetMaybe() = for {
    r6 <- getRMaybe(nested6Value)
    r5 <- getRMaybe(r6)
    r4 <- getRMaybe(r5)
    r3 <- getRMaybe(r4)
    r2 <- getRMaybe(r3)
    r1 <- getRMaybe(r2)
    i  <- getIMaybe(r1)
  } yield i
  @Benchmark def prismNested6GetMaybe() = (_r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _i).getMaybe(nested3Value)

}

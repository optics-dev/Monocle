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

  val nested3I = _r composePrism _r composePrism _r composePrism _i
  val nested6I = _r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _i

  @Benchmark def stdGetMaybe() = getIMaybe(intADT)
  @Benchmark def prismGetMaybe() = _i.getMaybe(intADT)

  @Benchmark def stdNested3GetMaybe() = for {
    r3 <- getRMaybe(nested3Value)
    r2 <- getRMaybe(r3)
    r1 <- getRMaybe(r2)
    i  <- getIMaybe(r1)
  } yield i
  @Benchmark def prismNested3GetMaybe() = nested3I.getMaybe(nested3Value)

  @Benchmark def stdNested6GetMaybe() = for {
    r6 <- getRMaybe(nested6Value)
    r5 <- getRMaybe(r6)
    r4 <- getRMaybe(r5)
    r3 <- getRMaybe(r4)
    r2 <- getRMaybe(r3)
    r1 <- getRMaybe(r2)
    i  <- getIMaybe(r1)
  } yield i
  @Benchmark def prismNested6GetMaybe() = nested6I.getMaybe(nested3Value)

  @Benchmark def stdModifySet() = getIMaybe(intADT).map(i => mkI(i+1)).getOrElse(intADT)
  @Benchmark def prismModifySet() = _i.modify(_ + 1)(intADT)

  @Benchmark def stdNested3Modify() = for {
    r3 <- getRMaybe(nested3Value)
    r2 <- getRMaybe(r3)
    r1 <- getRMaybe(r2)
    i  <- getIMaybe(r1)
  } yield mkR(mkR(mkR(mkI(i + 1))))
  @Benchmark def prismNested3Modify() = nested3I.modify(_ + 1)(nested3Value)

  @Benchmark def stdNested6Modify() = for {
    r6 <- getRMaybe(nested6Value)
    r5 <- getRMaybe(r6)
    r4 <- getRMaybe(r5)
    r3 <- getRMaybe(r4)
    r2 <- getRMaybe(r3)
    r1 <- getRMaybe(r2)
    i  <- getIMaybe(r1)
  } yield mkR(mkR(mkR(mkR(mkR(mkR(mkI(i + 1)))))))
  @Benchmark def prismNested6Modify() = nested6I.modify(_ + 1)(nested6Value)

}

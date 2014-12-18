package monocle.bench

import monocle.Prism
import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class MonoclePrismBench {

  val _i = Prism(getIMaybe)(mkI)
  val _s = Prism(getSMaybe)(mkS)
  val _r = Prism(getRMaybe)(mkR)

  val nested3I = _r composePrism _r composePrism _r composePrism _i
  val nested6I = _r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _i


  @Benchmark def getMaybe0() = _i.getMaybe(adt0)
  @Benchmark def getMaybe3() = nested3I.getMaybe(adt3)
  @Benchmark def getMaybe6() = nested6I.getMaybe(adt3)


  @Benchmark def modify0() = _i.modify(_ + 1)(adt0)
  @Benchmark def modify3() = nested3I.modify(_ + 1)(adt3)
  @Benchmark def modify6() = nested6I.modify(_ + 1)(adt6)

}

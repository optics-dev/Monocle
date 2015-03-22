package monocle.bench

import java.util.concurrent.TimeUnit

import monocle.Prism
import monocle.bench.BenchModel._
import monocle.bench.input.ADTInput
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class MonoclePrismBench extends PrismBench {

  val _i = Prism(getIMaybe)(mkI)
  val _r = Prism(getRMaybe)(mkR)

  val nested3I = _r composePrism _r composePrism _r composePrism _i
  val nested6I = _r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _r composePrism _i


  @Benchmark def getMaybe0(in: ADTInput): Option[Int] =
    _i.getMaybe(in.adt).toOption
  @Benchmark def getMaybe3(in: ADTInput): Option[Int] =
    nested3I.getMaybe(in.adt).toOption
  @Benchmark def getMaybe6(in: ADTInput): Option[Int] =
    nested6I.getMaybe(in.adt).toOption

  @Benchmark def modify0(in: ADTInput): ADT =
    _i.modify(_ + 1)(in.adt)
  @Benchmark def modify3(in: ADTInput): ADT =
    nested3I.modify(_ + 1)(in.adt)
  @Benchmark def modify6(in: ADTInput): ADT =
    nested6I.modify(_ + 1)(in.adt)
}

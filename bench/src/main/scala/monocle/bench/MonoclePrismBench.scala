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
  val _i = Prism(getIOption)(mkI)
  val _r = Prism(getROption)(mkR)

  val nested3I = _r andThen _r andThen _r andThen _i
  val nested6I = _r andThen _r andThen _r andThen _r andThen _r andThen _r andThen _i
  @Benchmark def getOption0(in: ADTInput): Option[Int] =
    _i.getOption(in.adt)
  @Benchmark def getOption3(in: ADTInput): Option[Int] =
    nested3I.getOption(in.adt)
  @Benchmark def getOption6(in: ADTInput): Option[Int] =
    nested6I.getOption(in.adt)

  @Benchmark def modify0(in: ADTInput): ADT =
    _i.modify(_ + 1)(in.adt)
  @Benchmark def modify3(in: ADTInput): ADT =
    nested3I.modify(_ + 1)(in.adt)
  @Benchmark def modify6(in: ADTInput): ADT =
    nested6I.modify(_ + 1)(in.adt)
}

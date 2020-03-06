package monocle.bench

import java.util.concurrent.TimeUnit

import monocle.bench.BenchModel._
import monocle.bench.input.ADTInput
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class StdPrismBench extends PrismBench {
  @Benchmark def getOption0(in: ADTInput): Option[Int] =
    getIOption(in.adt)
  @Benchmark def getOption3(in: ADTInput): Option[Int] =
    getROption(in.adt)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getIOption)
  @Benchmark def getOption6(in: ADTInput): Option[Int] =
    getROption(in.adt)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getIOption)

  @Benchmark def modify0(in: ADTInput): ADT =
    getIOption(in.adt).map(i => mkI(i + 1)).getOrElse(in.adt)
  @Benchmark def modify3(in: ADTInput): ADT =
    getROption(in.adt)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getIOption)
      .map(i => mkR(mkR(mkR(mkI(i + 1)))))
      .getOrElse(in.adt)
  @Benchmark def modify6(in: ADTInput): ADT =
    getROption(in.adt)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getROption)
      .flatMap(getIOption)
      .map(i => mkR(mkR(mkR(mkR(mkR(mkR(mkI(i + 1))))))))
      .getOrElse(in.adt)
}

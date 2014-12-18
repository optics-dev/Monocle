package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class StdPrismBench {

  @Benchmark def getMaybe0() =
    getIMaybe(adt0)
  @Benchmark def getMaybe3() =
    getRMaybe(adt3)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getIMaybe)
  @Benchmark def getMaybe6() =
    getRMaybe(adt6)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getIMaybe)


  @Benchmark def modify0() =
    getIMaybe(adt0).map(i => mkI(i+1)).getOrElse(adt0)
  @Benchmark def modify3() =
    getRMaybe(adt3)
    .flatMap(getRMaybe)
    .flatMap(getRMaybe)
    .flatMap(getIMaybe).map(i =>
      mkR(mkR(mkR(mkI(i + 1))))
    ).getOrElse(adt3)
  @Benchmark def modify6() =
    getRMaybe(adt6)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getRMaybe)
      .flatMap(getIMaybe).map(i =>
        mkR(mkR(mkR(mkR(mkR(mkR(mkI(i + 1)))))))
      ).getOrElse(adt6)
}

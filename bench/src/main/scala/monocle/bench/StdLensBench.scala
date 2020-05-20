package monocle.bench

import java.util.concurrent.TimeUnit

import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import org.openjdk.jmh.annotations._

// format: off
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class StdLensBench extends LensBench {

  @Benchmark def lensGet0(in: Nested0Input) = in.n0.i
  @Benchmark def lensGet3(in: Nested0Input) = in.n0.n.n.n.i
  @Benchmark def lensGet6(in: Nested0Input) = in.n0.n.n.n.n.n.n.i


  @Benchmark def lensSet0(in: Nested0Input) = in.n0.copy(i = 43)
  @Benchmark def lensSet3(in: Nested0Input) = in.n0.copy(n = in.n0.n.copy(n = in.n0.n.n.copy(n = in.n0.n.n.n.copy(i = 43))))
  @Benchmark def lensSet6(in: Nested0Input) = in.n0.copy(
    n = in.n0.n.copy(
      n = in.n0.n.n.copy(
        n = in.n0.n.n.n.copy(
          n = in.n0.n.n.n.n.copy(
            n = in.n0.n.n.n.n.n.copy(
              n = in.n0.n.n.n.n.n.n.copy(
                i = 43
              )))))))


  @Benchmark def lensModify0(in: Nested0Input) = in.n0.copy(i = in.n0.i + 1)
  @Benchmark def lensModify3(in: Nested0Input) = in.n0.copy(n = in.n0.n.copy(n = in.n0.n.n.copy(n = in.n0.n.n.n.copy(i = in.n0.n.n.n.i + 1))))
  @Benchmark def lensModify6(in: Nested0Input) = in.n0.copy(
    n = in.n0.n.copy(
      n = in.n0.n.n.copy(
        n = in.n0.n.n.n.copy(
          n = in.n0.n.n.n.n.copy(
            n = in.n0.n.n.n.n.n.copy(
              n = in.n0.n.n.n.n.n.n.copy(
                i = in.n0.n.n.n.n.n.n.i + 1
              )))))))


  @Benchmark def lensModifyF0(in: Nested0Input) = safeDivide(in.n0.i, 2).map(_i => in.n0.copy(i = _i))
  @Benchmark def lensModifyF3(in: Nested0Input) = safeDivide(in.n0.n.n.n.i, 2).map(_i =>
    in.n0.copy(n = in.n0.n.copy(n = in.n0.n.n.copy(n = in.n0.n.n.n.copy(i = _i))))
  )
  @Benchmark def lensModifyF6(in: Nested0Input) = safeDivide(in.n0.n.n.n.n.n.n.i, 2).map(_i => in.n0.copy(
    n = in.n0.n.copy(
      n = in.n0.n.n.copy(
        n = in.n0.n.n.n.copy(
          n = in.n0.n.n.n.n.copy(
            n = in.n0.n.n.n.n.n.copy(
              n = in.n0.n.n.n.n.n.n.copy(
                i = _i
              ))))))))
}

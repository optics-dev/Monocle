package monocle.bench.lens

import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import org.openjdk.jmh.annotations._

@State(Scope.Benchmark)
class LensStdBench {

  @Benchmark def lensSTDGet0(in: Nested0Input) = in.n0.i
  @Benchmark def lensSTDGet3(in: Nested0Input) = in.n0.n.n.n.i
  @Benchmark def lensSTDGet6(in: Nested0Input) = in.n0.n.n.n.n.n.n.i

  @Benchmark def lensSTDSet0(in: Nested0Input) = in.n0.copy(i = 43)
  @Benchmark def lensSTDSet3(in: Nested0Input) = in.n0.copy(n = in.n0.n.copy(n = in.n0.n.n.copy(n = in.n0.n.n.n.copy(i = 43))))
  @Benchmark def lensSTDSet6(in: Nested0Input) = in.n0.copy(
    n = in.n0.n.copy(
      n = in.n0.n.n.copy(
        n = in.n0.n.n.n.copy(
          n = in.n0.n.n.n.n.copy(
            n = in.n0.n.n.n.n.n.copy(
              n = in.n0.n.n.n.n.n.n.copy(
                i = 43
              )))))))

  @Benchmark def lensSTDModify0(in: Nested0Input) = in.n0.copy(i = in.n0.i + 1)
  @Benchmark def lensSTDModify3(in: Nested0Input) = in.n0.copy(n = in.n0.n.copy(n = in.n0.n.n.copy(n = in.n0.n.n.n.copy(i = in.n0.n.n.n.i + 1))))
  @Benchmark def lensSTDModify6(in: Nested0Input) = in.n0.copy(
    n = in.n0.n.copy(
      n = in.n0.n.n.copy(
        n = in.n0.n.n.n.copy(
          n = in.n0.n.n.n.n.copy(
            n = in.n0.n.n.n.n.n.copy(
              n = in.n0.n.n.n.n.n.n.copy(
                i = in.n0.n.n.n.n.n.n.i + 1
              )))))))

  @Benchmark def lensSTDModifyF0(in: Nested0Input) = halfEven(in.n0.i).map(_i => in.n0.copy(i = _i))
  @Benchmark def lensSTDModifyF3(in: Nested0Input) = halfEven(in.n0.n.n.n.i).map(_i =>
    in.n0.copy(n = in.n0.n.copy(n = in.n0.n.n.copy(n = in.n0.n.n.n.copy(i = _i))))
  )
  @Benchmark def lensSTDModifyF6(in: Nested0Input) = halfEven(in.n0.n.n.n.n.n.n.i).map(_i => in.n0.copy(
    n = in.n0.n.copy(
      n = in.n0.n.n.copy(
        n = in.n0.n.n.n.copy(
          n = in.n0.n.n.n.n.copy(
            n = in.n0.n.n.n.n.n.copy(
              n = in.n0.n.n.n.n.n.n.copy(
                i = _i
              ))))))))
}

package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class StdLensBench extends LensBench {

  @Benchmark def lensGet0() = n0.i
  @Benchmark def lensGet3() = n0.n.n.n.i
  @Benchmark def lensGet6() = n0.n.n.n.n.n.n.i


  @Benchmark def lensSet0() = n0.copy(i = 43)
  @Benchmark def lensSet3() = n0.copy(n = n0.n.copy(n = n0.n.n.copy(n = n0.n.n.n.copy(i = 43))))
  @Benchmark def lensSet6() = n0.copy(
    n = n0.n.copy(
      n = n0.n.n.copy(
        n = n0.n.n.n.copy(
          n = n0.n.n.n.n.copy(
            n = n0.n.n.n.n.n.copy(
              n = n0.n.n.n.n.n.n.copy(
                i = 43
              )))))))


  @Benchmark def lensModify0() = n0.copy(i = n0.i + 1)
  @Benchmark def lensModify3() = n0.copy(n = n0.n.copy(n = n0.n.n.copy(n = n0.n.n.n.copy(i = n0.n.n.n.i + 1))))
  @Benchmark def lensModify6() = n0.copy(
    n = n0.n.copy(
      n = n0.n.n.copy(
        n = n0.n.n.n.copy(
          n = n0.n.n.n.n.copy(
            n = n0.n.n.n.n.n.copy(
              n = n0.n.n.n.n.n.n.copy(
                i = n0.n.n.n.n.n.n.i + 1
              )))))))


  @Benchmark def lensModifyF0() = safeDivide(n0.i, 2).map(newAge => n0.copy(i = newAge))
  @Benchmark def lensModifyF3() = safeDivide(n0.n.n.n.i, 2).map(newI =>
    n0.copy(n = n0.n.copy(n = n0.n.n.copy(n = n0.n.n.n.copy(i = newI))))
  )
  @Benchmark def lensModifyF6() = safeDivide(n0.n.n.n.n.n.n.i, 2).map(newI => n0.copy(
    n = n0.n.copy(
      n = n0.n.n.copy(
        n = n0.n.n.n.copy(
          n = n0.n.n.n.n.copy(
            n = n0.n.n.n.n.n.copy(
              n = n0.n.n.n.n.n.n.copy(
                i = newI
              ))))))))
}

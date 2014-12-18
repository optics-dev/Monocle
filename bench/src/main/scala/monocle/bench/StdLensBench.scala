package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class StdLensBench {

  @Benchmark def get0() = n0.i
  @Benchmark def get3() = n0.n.n.n.i
  @Benchmark def get6() = n0.n.n.n.n.n.n.i


  @Benchmark def set0() = n0.copy(i = 43)
  @Benchmark def set3() = n0.copy(n = n0.n.copy(n = n0.n.n.copy(n = n0.n.n.n.copy(i = 43))))
  @Benchmark def set6() = n0.copy(
    n = n0.n.copy(
      n = n0.n.n.copy(
        n = n0.n.n.n.copy(
          n = n0.n.n.n.n.copy(
            n = n0.n.n.n.n.n.copy(
              n = n0.n.n.n.n.n.n.copy(
                i = 43
              )))))))


  @Benchmark def modify0() = n0.copy(i = n0.i + 1)
  @Benchmark def modify3() = n0.copy(n = n0.n.copy(n = n0.n.n.copy(n = n0.n.n.n.copy(i = n0.n.n.n.i + 1))))
  @Benchmark def modify6() = n0.copy(
    n = n0.n.copy(
      n = n0.n.n.copy(
        n = n0.n.n.n.copy(
          n = n0.n.n.n.n.copy(
            n = n0.n.n.n.n.n.copy(
              n = n0.n.n.n.n.n.n.copy(
                i = n0.n.n.n.n.n.n.i + 1
              )))))))


  @Benchmark def modifyF0() = safeDivide(n0.i, 2).map(newAge => n0.copy(i = newAge))
  @Benchmark def modifyF3() = n0.copy(n = n0.n.copy(n = n0.n.n.copy(n = n0.n.n.n.copy(i = n0.n.n.n.i + 1))))
  @Benchmark def modifyF6() = safeDivide(n0.n.n.n.n.n.n.i, 2).map(newI => n0.copy(
    n = n0.n.copy(
      n = n0.n.n.copy(
        n = n0.n.n.n.copy(
          n = n0.n.n.n.n.copy(
            n = n0.n.n.n.n.n.copy(
              n = n0.n.n.n.n.n.n.copy(
                i = 43
              ))))))))
}

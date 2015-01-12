package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class StdLensBench extends LensBench {

  @Benchmark def lensGet0() = arrayGet(_.i)
  @Benchmark def lensGet3() = arrayGet(_.n.n.n.i)
  @Benchmark def lensGet6() = arrayGet(_.n.n.n.n.n.n.i)


  @Benchmark def lensSet0() = arraySetModify(_n0 => _n0.copy(i = 43))
  @Benchmark def lensSet3() = arraySetModify(_n0 => _n0.copy(n = _n0.n.copy(n = _n0.n.n.copy(n = _n0.n.n.n.copy(i = 43)))))
  @Benchmark def lensSet6() = arraySetModify(_n0 => _n0.copy(
    n = _n0.n.copy(
      n = _n0.n.n.copy(
        n = _n0.n.n.n.copy(
          n = _n0.n.n.n.n.copy(
            n = _n0.n.n.n.n.n.copy(
              n = _n0.n.n.n.n.n.n.copy(
                i = 43
              )))))))
  )


  @Benchmark def lensModify0() = arraySetModify(_n0 => _n0.copy(i = _n0.i + 1))
  @Benchmark def lensModify3() = arraySetModify(_n0 => _n0.copy(n = _n0.n.copy(n = _n0.n.n.copy(n = _n0.n.n.n.copy(i = _n0.n.n.n.i + 1)))))
  @Benchmark def lensModify6() = arraySetModify(_n0 => _n0.copy(
    n = _n0.n.copy(
      n = _n0.n.n.copy(
        n = _n0.n.n.n.copy(
          n = _n0.n.n.n.n.copy(
            n = _n0.n.n.n.n.n.copy(
              n = _n0.n.n.n.n.n.n.copy(
                i = _n0.n.n.n.n.n.n.i + 1
              )))))))
  )


  @Benchmark def lensModifyF0() = arrayModifyMaybe(_n0 => safeDivide(_n0.i, 2).map(newAge => _n0.copy(i = newAge)))
  @Benchmark def lensModifyF3() = arrayModifyMaybe(_n0 => safeDivide(_n0.n.n.n.i, 2).map(newI =>
    _n0.copy(n = _n0.n.copy(n = _n0.n.n.copy(n = _n0.n.n.n.copy(i = newI))))
  ))
  @Benchmark def lensModifyF6() = arrayModifyMaybe(_n0 => safeDivide(_n0.n.n.n.n.n.n.i, 2).map(newI => _n0.copy(
    n = _n0.n.copy(
      n = _n0.n.n.copy(
        n = _n0.n.n.n.copy(
          n = _n0.n.n.n.n.copy(
            n = _n0.n.n.n.n.n.copy(
              n = _n0.n.n.n.n.n.n.copy(
                i = newI
              ))))))))
  )
}

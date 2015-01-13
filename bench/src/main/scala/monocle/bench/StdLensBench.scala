package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class StdLensBench extends LensBench {

  @Benchmark def lensGet0() = {var r,i = 0; while (i < n0s.length) { val v = n0s(i).i            ; if (v > r){r = v}; i = i + 1;}; r}
  @Benchmark def lensGet3() = {var r,i = 0; while (i < n0s.length) { val v = n0s(i).n.n.n.i      ; if (v > r){r = v}; i = i + 1;}; r}
  @Benchmark def lensGet6() = {var r,i = 0; while (i < n0s.length) { val v = n0s(i).n.n.n.n.n.n.i; if (v > r){r = v}; i = i + 1;}; r}


  @Benchmark def lensSet0() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val _n0 = n0s(i)
    val v = _n0.copy(i = 43)
    if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensSet3() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val _n0 = n0s(i)
    val v = _n0.copy(n = _n0.n.copy(n = _n0.n.n.copy(n = _n0.n.n.n.copy(i = 43))))
    if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensSet6() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val _n0 = n0s(i)
    val v = _n0.copy(
      n = _n0.n.copy(
        n = _n0.n.n.copy(
          n = _n0.n.n.n.copy(
            n = _n0.n.n.n.n.copy(
              n = _n0.n.n.n.n.n.copy(
                n = _n0.n.n.n.n.n.n.copy(
                  i = 43
                )))))))
    if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}


  @Benchmark def lensModify0() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val _n0 = n0s(i)
    val v = _n0.copy(i = _n0.i + 1)
    if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensModify3() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val _n0 = n0s(i)
    val v = _n0.copy(n = _n0.n.copy(n = _n0.n.n.copy(n = _n0.n.n.n.copy(i = _n0.n.n.n.i + 1))))
    if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensModify6() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val _n0 = n0s(i)
    val v = _n0.copy(
      n = _n0.n.copy(
        n = _n0.n.n.copy(
          n = _n0.n.n.n.copy(
            n = _n0.n.n.n.n.copy(
              n = _n0.n.n.n.n.n.copy(
                n = _n0.n.n.n.n.n.n.copy(
                  i = _n0.n.n.n.n.n.n.i + 1
                )))))))
    if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}


  @Benchmark def lensModifyF0() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) { val _n0 = n0s(i)
    val v = safeDivide(_n0.i, 2).map(newAge => _n0.copy(i = newAge))
    v.map{_v => if(_v.i > r){r = _v.i; res = v;}}; i = i + 1;}; res}
  @Benchmark def lensModifyF3() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) { val _n0 = n0s(i)
    val v = safeDivide(_n0.n.n.n.i, 2).map(newI =>
      _n0.copy(n = _n0.n.copy(n = _n0.n.n.copy(n = _n0.n.n.n.copy(i = newI))))
    )
    v.map{_v => if(_v.i > r){r = _v.i; res = v;}}; i = i + 1;}; res}
  @Benchmark def lensModifyF6() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) { val _n0 = n0s(i)
    val v = safeDivide(_n0.n.n.n.n.n.n.i, 2).map(newI => _n0.copy(
      n = _n0.n.copy(
        n = _n0.n.n.copy(
          n = _n0.n.n.n.copy(
            n = _n0.n.n.n.n.copy(
              n = _n0.n.n.n.n.n.copy(
                n = _n0.n.n.n.n.n.n.copy(
                  i = newI
                ))))))))
    v.map{_v => if(_v.i > r){r = _v.i; res = v;}}; i = i + 1;}; res}
}

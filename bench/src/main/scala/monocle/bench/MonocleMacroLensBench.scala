package monocle.bench

import monocle.bench.BenchModel._
import monocle.macros.Lenser
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class MonocleMacroLensBench extends LensBench {


  val _n1 = Lenser[Nested0](_.n)
  val _n2 = Lenser[Nested1](_.n)
  val _n3 = Lenser[Nested2](_.n)
  val _n4 = Lenser[Nested3](_.n)
  val _n5 = Lenser[Nested4](_.n)
  val _n6 = Lenser[Nested5](_.n)

  val _n0_i = Lenser[Nested0](_.i)
  val _n3_i = Lenser[Nested3](_.i)
  val _n6_i = Lenser[Nested6](_.i)

  val _n0Ton3I = _n1 composeLens _n2 composeLens _n3 composeLens _n3_i
  val _n0Ton6I = _n1 composeLens _n2 composeLens _n3 composeLens _n4 composeLens _n5 composeLens _n6 composeLens _n6_i


  @Benchmark def lensGet0() = {var r,i = 0; while (i < n0s.length) { val v = _n0_i.get(n0s(i))   ; if (v > r){r = v}; i = i + 1;}; r}
  @Benchmark def lensGet3() = {var r,i = 0; while (i < n0s.length) { val v = _n0Ton3I.get(n0s(i)); if (v > r){r = v}; i = i + 1;}; r}
  @Benchmark def lensGet6() = {var r,i = 0; while (i < n0s.length) { val v = _n0Ton6I.get(n0s(i)); if (v > r){r = v}; i = i + 1;}; r}


  @Benchmark def lensSet0() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0_i.set(43)(n0s(i))   ; if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensSet3() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton3I.set(43)(n0s(i)); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensSet6() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton6I.set(43)(n0s(i)); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}


  @Benchmark def lensModify0() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0_i.modify(_ + 1)(n0s(i))   ; if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensModify3() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton3I.modify(_ + 1)(n0s(i)); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensModify6() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton6I.modify(_ + 1)(n0s(i)); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}


  @Benchmark def lensModifyF0() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) {val v = _n0_i.modifyF(safeDivide(_, 2))(n0s(i))   ; v.map{_v => if (_v.i > r){r = _v.i; res = v}}; i = i + 1;}; res}
  @Benchmark def lensModifyF3() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) {val v = _n0Ton3I.modifyF(safeDivide(_, 2))(n0s(i)); v.map{_v => if (_v.i > r){r = _v.i; res = v}}; i = i + 1;}; res}
  @Benchmark def lensModifyF6() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) {val v = _n0Ton6I.modifyF(safeDivide(_, 2))(n0s(i)); v.map{_v => if (_v.i > r){r = _v.i; res = v}}; i = i + 1;}; res}

}

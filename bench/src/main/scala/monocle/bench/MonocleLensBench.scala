package monocle.bench

import monocle.Lens
import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class MonocleLensBench extends LensBench {


  val _n1 = Lens[Nested0, Nested1](_.n)(n2 => n1 => n1.copy(n = n2))
  val _n2 = Lens[Nested1, Nested2](_.n)(n3 => n2 => n2.copy(n = n3))
  val _n3 = Lens[Nested2, Nested3](_.n)(n4 => n3 => n3.copy(n = n4))
  val _n4 = Lens[Nested3, Nested4](_.n)(n5 => n4 => n4.copy(n = n5))
  val _n5 = Lens[Nested4, Nested5](_.n)(n6 => n5 => n5.copy(n = n6))
  val _n6 = Lens[Nested5, Nested6](_.n)(n7 => n6 => n6.copy(n = n7))

  val _n0_i = Lens[Nested0, Int](_.i)(i => n => n.copy(i = i))
  val _n3_i = Lens[Nested3, Int](_.i)(i => n => n.copy(i = i))
  val _n6_i = Lens[Nested6, Int](_.i)(i => n => n.copy(i = i))

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

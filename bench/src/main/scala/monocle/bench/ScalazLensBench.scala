package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.{Lens, Maybe}

@State(Scope.Benchmark)
class ScalazLensBench extends LensBench {

  val _n1 = Lens.lensg[Nested0, Nested1](n0 => n1 => n0.copy(n = n1), _.n)
  val _n2 = Lens.lensg[Nested1, Nested2](n1 => n2 => n1.copy(n = n2), _.n)
  val _n3 = Lens.lensg[Nested2, Nested3](n2 => n3 => n2.copy(n = n3), _.n)
  val _n4 = Lens.lensg[Nested3, Nested4](n3 => n4 => n3.copy(n = n4), _.n)
  val _n5 = Lens.lensg[Nested4, Nested5](n4 => n5 => n4.copy(n = n5), _.n)
  val _n6 = Lens.lensg[Nested5, Nested6](n5 => n6 => n5.copy(n = n6), _.n)

  val _n0_i = Lens.lensg[Nested0, Int](n => i => n.copy(i = i), _.i)
  val _n3_i = Lens.lensg[Nested3, Int](n => i => n.copy(i = i), _.i)
  val _n6_i = Lens.lensg[Nested6, Int](n => i => n.copy(i = i), _.i)

  val _n0Ton3I = _n1 >=> _n2 >=> _n3 >=> _n3_i
  val _n0Ton6I = _n1 >=> _n2 >=> _n3 >=> _n4 >=> _n5 >=> _n6 >=> _n6_i


  @Benchmark def lensGet0() = {var r,i = 0; while (i < n0s.length) { val v = _n0_i.get(n0s(i))   ; if (v > r){r = v}; i = i + 1;}; r}
  @Benchmark def lensGet3() = {var r,i = 0; while (i < n0s.length) { val v = _n0Ton3I.get(n0s(i)); if (v > r){r = v}; i = i + 1;}; r}
  @Benchmark def lensGet6() = {var r,i = 0; while (i < n0s.length) { val v = _n0Ton6I.get(n0s(i)); if (v > r){r = v}; i = i + 1;}; r}


  @Benchmark def lensSet0() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0_i.set(n0s(i), 43)   ; if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensSet3() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton3I.set(n0s(i), 43); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensSet6() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton6I.set(n0s(i), 43); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}


  @Benchmark def lensModify0() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0_i.mod(   _ + 1, n0s(i)); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensModify3() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton3I.mod(_ + 1, n0s(i)); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}
  @Benchmark def lensModify6() = {var r,i = 0; var res: Nested0 = null; while (i < n0s.length) { val v = _n0Ton6I.mod(_ + 1, n0s(i)); if (v.i > r){r = v.i; res = v}; i = i + 1;}; res}


  @Benchmark def lensModifyF0() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) {val v = _n0_i.modf(   safeDivide(_, 2), n0s(i)); v.map{_v => if (_v.i > r){r = _v.i; res = v}}; i = i + 1;}; res}
  @Benchmark def lensModifyF3() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) {val v = _n0Ton3I.modf(safeDivide(_, 2), n0s(i)); v.map{_v => if (_v.i > r){r = _v.i; res = v}}; i = i + 1;}; res}
  @Benchmark def lensModifyF6() = {var r,i = 0; var res: Maybe[Nested0] = Maybe.empty; while (i < n0s.length) {val v = _n0Ton6I.modf(safeDivide(_, 2), n0s(i)); v.map{_v => if (_v.i > r){r = _v.i; res = v}}; i = i + 1;}; res}

}

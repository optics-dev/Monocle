package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Lens

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


  @Benchmark def lensGet0() = arrayGet(_n0_i.get)
  @Benchmark def lensGet3() = arrayGet(_n0Ton3I.get)
  @Benchmark def lensGet6() = arrayGet(_n0Ton6I.get)


  @Benchmark def lensSet0() = arraySetModify(_n0_i.set(_, 43))
  @Benchmark def lensSet3() = arraySetModify(_n0Ton3I.set(_, 43))
  @Benchmark def lensSet6() = arraySetModify(_n0Ton6I.set(_, 43))


  @Benchmark def lensModify0() = arraySetModify(_n0_i.mod(_ + 1, _))
  @Benchmark def lensModify3() = arraySetModify(_n0Ton3I.mod(_ + 1, _))
  @Benchmark def lensModify6() = arraySetModify(_n0Ton6I.mod(_ + 1, _))


  @Benchmark def lensModifyF0() = arrayModifyMaybe(_n0_i.modf(safeDivide(_, 2), _))
  @Benchmark def lensModifyF3() = arrayModifyMaybe(_n0Ton3I.modf(safeDivide(_, 2), _))
  @Benchmark def lensModifyF6() = arrayModifyMaybe(_n0Ton6I.modf(safeDivide(_, 2), _))

}

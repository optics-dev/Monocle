package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Lens

@State(Scope.Benchmark)
class ScalazLensBench {

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


  @Benchmark def get0() = _n0_i.get(n0)
  @Benchmark def get3() = _n0Ton3I.get(n0)
  @Benchmark def get6() = _n0Ton6I.get(n0)


  @Benchmark def set0() = _n0_i.set(n0, 43)
  @Benchmark def set3() = _n0Ton3I.set(n0, 43)
  @Benchmark def set6() = _n0Ton6I.set(n0, 43)


  @Benchmark def modify0() = _n0_i.mod(_ + 1, n0)
  @Benchmark def modify3() = _n0Ton3I.mod(_ + 1, n0)
  @Benchmark def modify6() = _n0Ton6I.mod(_ + 1, n0)


  @Benchmark def modifyF0() = _n0_i.modf(safeDivide(_, 2), n0)
  @Benchmark def modifyF3() = _n0Ton3I.modf(safeDivide(_, 2), n0)
  @Benchmark def modifyF6() = _n0Ton6I.modf(safeDivide(_, 2), n0)

}

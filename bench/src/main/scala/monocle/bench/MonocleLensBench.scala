package monocle.bench

import monocle.Lens
import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class MonocleLensBench {


  val _n2 = Lens[Nested0, Nested1](_.n)(n2 => n1 => n1.copy(n = n2))
  val _n3 = Lens[Nested1, Nested2](_.n)(n3 => n2 => n2.copy(n = n3))
  val _n4 = Lens[Nested2, Nested3](_.n)(n4 => n3 => n3.copy(n = n4))
  val _n5 = Lens[Nested3, Nested4](_.n)(n5 => n4 => n4.copy(n = n5))
  val _n6 = Lens[Nested4, Nested5](_.n)(n6 => n5 => n5.copy(n = n6))
  val _n7 = Lens[Nested5, Nested6](_.n)(n7 => n6 => n6.copy(n = n7))

  val _n1_i = Lens[Nested0, Int](_.i)(i => n => n.copy(i = i))
  val _n4_i = Lens[Nested3, Int](_.i)(i => n => n.copy(i = i))
  val _n7_i = Lens[Nested6, Int](_.i)(i => n => n.copy(i = i))

  val _n1Ton4I = _n2 composeLens _n3 composeLens _n4 composeLens _n4_i
  val _n1Ton7I = _n2 composeLens _n3 composeLens _n4 composeLens _n5 composeLens _n6 composeLens _n7 composeLens _n7_i



  @Benchmark def get0() = _n1_i.get(n0)
  @Benchmark def get3() = _n1Ton4I.get(n0)
  @Benchmark def get6() = _n1Ton7I.get(n0)


  @Benchmark def set0() = _n1_i.set(43)(n0)
  @Benchmark def set3() = _n1Ton4I.set(43)(n0)
  @Benchmark def set6() = _n1Ton7I.set(43)(n0)


  @Benchmark def modify0() = _n1_i.modify(_ + 1)(n0)
  @Benchmark def modify3() = _n1Ton4I.modify(_ + 1)(n0)
  @Benchmark def modify6() = _n1Ton7I.modify(_ + 1)(n0)


  @Benchmark def modifyF0() = _n1_i.modifyF(safeDivide(_, 2))(n0)
  @Benchmark def modifyF3() = _n1Ton4I.modifyF(safeDivide(_, 2))(n0)
  @Benchmark def modifyF6() = _n1Ton7I.modifyF(safeDivide(_, 2))(n0)

}

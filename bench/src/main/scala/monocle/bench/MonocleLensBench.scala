package monocle.bench

import java.util.concurrent.TimeUnit

import monocle.Lens
import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import org.openjdk.jmh.annotations._

import cats.instances.option._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
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

  val _n0Ton3I = _n1 andThenLens _n2 andThenLens _n3 andThenLens _n3_i
  val _n0Ton6I = _n1 andThenLens _n2 andThenLens _n3 andThenLens _n4 andThenLens _n5 andThenLens _n6 andThenLens _n6_i


  @Benchmark def lensGet0(in: Nested0Input) = _n0_i.get(in.n0)
  @Benchmark def lensGet3(in: Nested0Input) = _n0Ton3I.get(in.n0)
  @Benchmark def lensGet6(in: Nested0Input) = _n0Ton6I.get(in.n0)


  @Benchmark def lensSet0(in: Nested0Input) = _n0_i.set(43)(in.n0)
  @Benchmark def lensSet3(in: Nested0Input) = _n0Ton3I.set(43)(in.n0)
  @Benchmark def lensSet6(in: Nested0Input) = _n0Ton6I.set(43)(in.n0)


  @Benchmark def lensModify0(in: Nested0Input) = _n0_i.modify(_ + 1)(in.n0)
  @Benchmark def lensModify3(in: Nested0Input) = _n0Ton3I.modify(_ + 1)(in.n0)
  @Benchmark def lensModify6(in: Nested0Input) = _n0Ton6I.modify(_ + 1)(in.n0)


  @Benchmark def lensModifyF0(in: Nested0Input) = _n0_i.modifyF(safeDivide(_, 2))(in.n0)
  @Benchmark def lensModifyF3(in: Nested0Input) = _n0Ton3I.modifyF(safeDivide(_, 2))(in.n0)
  @Benchmark def lensModifyF6(in: Nested0Input) = _n0Ton6I.modifyF(safeDivide(_, 2))(in.n0)

}

package monocle.bench

import java.util.concurrent.TimeUnit

import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import monocle.macros.GenLens
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class MonocleMacroLensBench extends LensBench {

  val _n1 = GenLens[Nested0](_.n)
  val _n2 = GenLens[Nested1](_.n)
  val _n3 = GenLens[Nested2](_.n)
  val _n4 = GenLens[Nested3](_.n)
  val _n5 = GenLens[Nested4](_.n)
  val _n6 = GenLens[Nested5](_.n)

  val _n0_i = GenLens[Nested0](_.i)
  val _n3_i = GenLens[Nested3](_.i)
  val _n6_i = GenLens[Nested6](_.i)

  val _n0Ton3I = _n1 composeLens _n2 composeLens _n3 composeLens _n3_i
  val _n0Ton6I = _n1 composeLens _n2 composeLens _n3 composeLens _n4 composeLens _n5 composeLens _n6 composeLens _n6_i

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

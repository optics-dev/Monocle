package monocle.bench.lens

import monocle.Lens
import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.std.option._

@State(Scope.Benchmark)
class LensMOBench {

  val n1 = Lens[Nested0, Nested1](_.n)(n2 => n1 => n1.copy(n = n2))
  val n2 = Lens[Nested1, Nested2](_.n)(n3 => n2 => n2.copy(n = n3))
  val n3 = Lens[Nested2, Nested3](_.n)(n4 => n3 => n3.copy(n = n4))
  val n4 = Lens[Nested3, Nested4](_.n)(n5 => n4 => n4.copy(n = n5))
  val n5 = Lens[Nested4, Nested5](_.n)(n6 => n5 => n5.copy(n = n6))
  val n6 = Lens[Nested5, Nested6](_.n)(n7 => n6 => n6.copy(n = n7))

  val n0_i = Lens[Nested0, Int](_.i)(i => n => n.copy(i = i))
  val n3_i = Lens[Nested3, Int](_.i)(i => n => n.copy(i = i))
  val n6_i = Lens[Nested6, Int](_.i)(i => n => n.copy(i = i))

  val n0Ton3I = n1 composeLens n2 composeLens n3 composeLens n3_i
  val n0Ton6I = n1 composeLens n2 composeLens n3 composeLens n4 composeLens n5 composeLens n6 composeLens n6_i

  @Benchmark def LensGet0(in: Nested0Input) = n0_i.get(in.n0)
  @Benchmark def LensGet3(in: Nested0Input) = n0Ton3I.get(in.n0)
  @Benchmark def LensGet6(in: Nested0Input) = n0Ton6I.get(in.n0)

  @Benchmark def LensSet0(in: Nested0Input) = n0_i.set(43)(in.n0)
  @Benchmark def LensSet3(in: Nested0Input) = n0Ton3I.set(43)(in.n0)
  @Benchmark def LensSet6(in: Nested0Input) = n0Ton6I.set(43)(in.n0)

  @Benchmark def LensModify0(in: Nested0Input) = n0_i.modify(_ + 1)(in.n0)
  @Benchmark def LensModify3(in: Nested0Input) = n0Ton3I.modify(_ + 1)(in.n0)
  @Benchmark def LensModify6(in: Nested0Input) = n0Ton6I.modify(_ + 1)(in.n0)

  @Benchmark def LensModifyF0(in: Nested0Input) = n0_i.modifyF(halfEven)(in.n0)
  @Benchmark def LensModifyF3(in: Nested0Input) = n0Ton3I.modifyF(halfEven)(in.n0)
  @Benchmark def LensModifyF6(in: Nested0Input) = n0Ton6I.modifyF(halfEven)(in.n0)
}
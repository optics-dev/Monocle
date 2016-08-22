package monocle.bench.lens

import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import monocle.macros.GenLens
import org.openjdk.jmh.annotations._
import scalaz.std.option._

@State(Scope.Benchmark)
class LensMacroBench {

  val n1 = GenLens[Nested0](_.n)
  val n2 = GenLens[Nested1](_.n)
  val n3 = GenLens[Nested2](_.n)
  val n4 = GenLens[Nested3](_.n)
  val n5 = GenLens[Nested4](_.n)
  val n6 = GenLens[Nested5](_.n)

  val n0_i = GenLens[Nested0](_.i)
  val n3_i = GenLens[Nested3](_.i)
  val n6_i = GenLens[Nested6](_.i)

  val n0Ton3I = n1 composeLens n2 composeLens n3 composeLens n3_i
  val n0Ton6I = n1 composeLens n2 composeLens n3 composeLens n4 composeLens n5 composeLens n6 composeLens n6_i

  @Benchmark def lensMACROGet0(in: Nested0Input) = n0_i.get(in.n0)
  @Benchmark def lensMACROGet3(in: Nested0Input) = n0Ton3I.get(in.n0)
  @Benchmark def lensMACROGet6(in: Nested0Input) = n0Ton6I.get(in.n0)

  @Benchmark def lensMACROSet0(in: Nested0Input) = n0_i.set(43)(in.n0)
  @Benchmark def lensMACROSet3(in: Nested0Input) = n0Ton3I.set(43)(in.n0)
  @Benchmark def lensMACROSet6(in: Nested0Input) = n0Ton6I.set(43)(in.n0)

  @Benchmark def lensMACROModify0(in: Nested0Input) = n0_i.modify(_ + 1)(in.n0)
  @Benchmark def lensMACROModify3(in: Nested0Input) = n0Ton3I.modify(_ + 1)(in.n0)
  @Benchmark def lensMACROModify6(in: Nested0Input) = n0Ton6I.modify(_ + 1)(in.n0)

  @Benchmark def lensMACROModifyF0(in: Nested0Input) = n0_i.modifyF(halfEven)(in.n0)
  @Benchmark def lensMACROModifyF3(in: Nested0Input) = n0Ton3I.modifyF(halfEven)(in.n0)
  @Benchmark def lensMACROModifyF6(in: Nested0Input) = n0Ton6I.modifyF(halfEven)(in.n0)

}
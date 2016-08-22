package monocle.bench.lens

import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import org.openjdk.jmh.annotations._

import scalaz.Lens
import scalaz.std.option._

@State(Scope.Benchmark)
class LensScalazBench {

  val n1 = Lens.lensg[Nested0, Nested1](n0 => n1 => n0.copy(n = n1), _.n)
  val n2 = Lens.lensg[Nested1, Nested2](n1 => n2 => n1.copy(n = n2), _.n)
  val n3 = Lens.lensg[Nested2, Nested3](n2 => n3 => n2.copy(n = n3), _.n)
  val n4 = Lens.lensg[Nested3, Nested4](n3 => n4 => n3.copy(n = n4), _.n)
  val n5 = Lens.lensg[Nested4, Nested5](n4 => n5 => n4.copy(n = n5), _.n)
  val n6 = Lens.lensg[Nested5, Nested6](n5 => n6 => n5.copy(n = n6), _.n)

  val n0_i = Lens.lensg[Nested0, Int](n => i => n.copy(i = i), _.i)
  val n3_i = Lens.lensg[Nested3, Int](n => i => n.copy(i = i), _.i)
  val n6_i = Lens.lensg[Nested6, Int](n => i => n.copy(i = i), _.i)

  val n0Ton3I = n1 >=> n2 >=> n3 >=> n3_i
  val n0Ton6I = n1 >=> n2 >=> n3 >=> n4 >=> n5 >=> n6 >=> n6_i

  @Benchmark def lensSCALAZGet0(in: Nested0Input) = n0_i.get(in.n0)
  @Benchmark def lensSCALAZGet3(in: Nested0Input) = n0Ton3I.get(in.n0)
  @Benchmark def lensSCALAZGet6(in: Nested0Input) = n0Ton6I.get(in.n0)

  @Benchmark def lensSCALAZSet0(in: Nested0Input) = n0_i.set(in.n0, 43)
  @Benchmark def lensSCALAZSet3(in: Nested0Input) = n0Ton3I.set(in.n0, 43)
  @Benchmark def lensSCALAZSet6(in: Nested0Input) = n0Ton6I.set(in.n0, 43)

  @Benchmark def lensSCALAZModify0(in: Nested0Input) = n0_i.mod(_ + 1, in.n0)
  @Benchmark def lensSCALAZModify3(in: Nested0Input) = n0Ton3I.mod(_ + 1, in.n0)
  @Benchmark def lensSCALAZModify6(in: Nested0Input) = n0Ton6I.mod(_ + 1, in.n0)

  @Benchmark def lensSCALAZModifyF0(in: Nested0Input) = n0_i.modf(halfEven, in.n0)
  @Benchmark def lensSCALAZModifyF3(in: Nested0Input) = n0Ton3I.modf(halfEven, in.n0)
  @Benchmark def lensSCALAZModifyF6(in: Nested0Input) = n0Ton6I.modf(halfEven, in.n0)

}

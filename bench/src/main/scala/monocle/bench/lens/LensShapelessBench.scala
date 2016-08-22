package monocle.bench.lens

import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import org.openjdk.jmh.annotations._
import shapeless._

@State(Scope.Benchmark)
class LensShapelessBench {

  val _n1 = lens[Nested0].n
  val _n2 = lens[Nested1].n
  val _n3 = lens[Nested2].n
  val _n4 = lens[Nested3].n
  val _n5 = lens[Nested4].n
  val _n6 = lens[Nested5].n

  val _n0_i = lens[Nested0].i
  val _n3_i = lens[Nested3].i
  val _n6_i = lens[Nested6].i

  val _n0Ton3I = _n3_i compose _n3 compose _n2 compose _n1
  val _n0Ton6I = _n6_i compose _n6 compose _n5 compose _n4 compose _n3 compose _n2 compose _n1

  @Benchmark def lensSHAPELESSGet0(in: Nested0Input) = _n0_i.get(in.n0)
  @Benchmark def lensSHAPELESSGet3(in: Nested0Input) = _n0Ton3I.get(in.n0)
  @Benchmark def lensSHAPELESSGet6(in: Nested0Input) = _n0Ton6I.get(in.n0)

  @Benchmark def lensSHAPELESSSet0(in: Nested0Input) = _n0_i.set(in.n0)(43)
  @Benchmark def lensSHAPELESSSet3(in: Nested0Input) = _n0Ton3I.set(in.n0)(43)
  @Benchmark def lensSHAPELESSSet6(in: Nested0Input) = _n0Ton6I.set(in.n0)(43)

  @Benchmark def lensSHAPELESSModify0(in: Nested0Input) = _n0_i.modify(in.n0)(_ + 1)
  @Benchmark def lensSHAPELESSModify3(in: Nested0Input) = _n0Ton3I.modify(in.n0)(_ + 1)
  @Benchmark def lensSHAPELESSModify6(in: Nested0Input) = _n0Ton6I.modify(in.n0)(_ + 1)

}
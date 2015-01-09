package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}
import shapeless._

import scalaz.Maybe


@State(Scope.Benchmark)
class ShapelessLensBench extends LensBench {

  val _n1 = lens[Nested0] >> 2
  val _n2 = lens[Nested1] >> 2
  val _n3 = lens[Nested2] >> 2
  val _n4 = lens[Nested3] >> 2
  val _n5 = lens[Nested4] >> 2
  val _n6 = lens[Nested5] >> 2

  val _n0_i = lens[Nested0] >> 1
  val _n3_i = lens[Nested3] >> 1
  val _n6_i = lens[Nested6] >> 1

  val _n0Ton3I = _n3_i compose _n3 compose _n2 compose _n1
  val _n0Ton6I = _n6_i compose _n6 compose _n5 compose _n4 compose _n3 compose _n2 compose _n1


  @Benchmark def lensGet0() = _n0_i.get(n0)
  @Benchmark def lensGet3() = _n0Ton3I.get(n0)
  @Benchmark def lensGet6() = _n0Ton6I.get(n0)


  @Benchmark def lensSet0() = _n0_i.set(n0)(43)
  @Benchmark def lensSet3() = _n0Ton3I.set(n0)(43)
  @Benchmark def lensSet6() = _n0Ton6I.set(n0)(43)


  @Benchmark def lensModify0() = _n0_i.modify(n0)(_ + 1)
  @Benchmark def lensModify3() = _n0Ton3I.modify(n0)(_ + 1)
  @Benchmark def lensModify6() = _n0Ton6I.modify(n0)(_ + 1)


  def lensModifyF0(): Maybe[Nested0] = ???
  def lensModifyF3(): Maybe[Nested0] = ???
  def lensModifyF6(): Maybe[Nested0] = ???
}
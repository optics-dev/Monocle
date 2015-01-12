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


  @Benchmark def lensGet0() = arrayGet(_n0_i.get)
  @Benchmark def lensGet3() = arrayGet(_n0Ton3I.get)
  @Benchmark def lensGet6() = arrayGet(_n0Ton6I.get)


  @Benchmark def lensSet0() = arraySetModify(_n0_i.set(_)(43))
  @Benchmark def lensSet3() = arraySetModify(_n0Ton3I.set(_)(43))
  @Benchmark def lensSet6() = arraySetModify(_n0Ton6I.set(_)(43))


  @Benchmark def lensModify0() = arraySetModify(_n0_i.modify(_)(_ + 1))
  @Benchmark def lensModify3() = arraySetModify(_n0Ton3I.modify(_)(_ + 1))
  @Benchmark def lensModify6() = arraySetModify(_n0Ton6I.modify(_)(_ + 1))


  def lensModifyF0(): Maybe[Nested0] = ???
  def lensModifyF3(): Maybe[Nested0] = ???
  def lensModifyF6(): Maybe[Nested0] = ???
}
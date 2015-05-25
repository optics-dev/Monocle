package monocle.bench

import java.util.concurrent.TimeUnit

import monocle.bench.BenchModel._
import monocle.bench.input.Nested0Input
import org.openjdk.jmh.annotations._
import shapeless._

import scalaz.Maybe

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class ShapelessLensBench extends LensBench {

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


  @Benchmark def lensGet0(in: Nested0Input) = _n0_i.get(in.n0)
  @Benchmark def lensGet3(in: Nested0Input) = _n0Ton3I.get(in.n0)
  @Benchmark def lensGet6(in: Nested0Input) = _n0Ton6I.get(in.n0)


  @Benchmark def lensSet0(in: Nested0Input) = _n0_i.set(in.n0)(43)
  @Benchmark def lensSet3(in: Nested0Input) = _n0Ton3I.set(in.n0)(43)
  @Benchmark def lensSet6(in: Nested0Input) = _n0Ton6I.set(in.n0)(43)


  @Benchmark def lensModify0(in: Nested0Input) = _n0_i.modify(in.n0)(_ + 1)
  @Benchmark def lensModify3(in: Nested0Input) = _n0Ton3I.modify(in.n0)(_ + 1)
  @Benchmark def lensModify6(in: Nested0Input) = _n0Ton6I.modify(in.n0)(_ + 1)


  def lensModifyF0(in: Nested0Input): Maybe[Nested0] = ???
  def lensModifyF3(in: Nested0Input): Maybe[Nested0] = ???
  def lensModifyF6(in: Nested0Input): Maybe[Nested0] = ???

}
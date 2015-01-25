package monocle.bench

import monocle.Iso
import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class MonocleIsoBench extends IsoBench {

  val w0_w1 = Iso[IntWrapper0, IntWrapper1](w => IntWrapper1(w.i))(w => IntWrapper0(w.i))
  val w1_w2 = Iso[IntWrapper1, IntWrapper2](w => IntWrapper2(w.i))(w => IntWrapper1(w.i))
  val w2_w3 = Iso[IntWrapper2, IntWrapper3](w => IntWrapper3(w.i))(w => IntWrapper2(w.i))
  val w3_w4 = Iso[IntWrapper3, IntWrapper4](w => IntWrapper4(w.i))(w => IntWrapper3(w.i))
  val w4_w5 = Iso[IntWrapper4, IntWrapper5](w => IntWrapper5(w.i))(w => IntWrapper4(w.i))
  val w5_w6 = Iso[IntWrapper5, IntWrapper6](w => IntWrapper6(w.i))(w => IntWrapper5(w.i))

  val w0_i = Iso[IntWrapper0, Int](_.i)(IntWrapper0.apply)
  val w3_i = Iso[IntWrapper3, Int](_.i)(IntWrapper3.apply)
  val w6_i = Iso[IntWrapper6, Int](_.i)(IntWrapper6.apply)

  val w0_w3_i = w0_w1 composeIso w1_w2 composeIso w2_w3 composeIso w3_i
  val w0_w6_i = w0_w1 composeIso w1_w2 composeIso w2_w3 composeIso w3_w4 composeIso w4_w5 composeIso w5_w6 composeIso w6_i

  val i_w0    = w0_i.reverse
  val i_w3_w0 = w0_w3_i.reverse
  val i_w6_w0 = w0_w6_i.reverse


  @Benchmark def get0(): Int = w0_i.get(w0)
  @Benchmark def get6(): Int = w0_w3_i.get(w0)
  @Benchmark def get3(): Int = w0_w6_i.get(w0)

  @Benchmark def reverseGet0: IntWrapper0 = w0_i.reverseGet(i)
  @Benchmark def reverseGet3: IntWrapper0 = w0_w3_i.reverseGet(i)
  @Benchmark def reverseGet6: IntWrapper0 = w0_w6_i.reverseGet(i)

  @Benchmark def reverse0: IntWrapper0 = i_w0.get(i)
  @Benchmark def reverse3: IntWrapper0 = i_w3_w0.get(i)
  @Benchmark def reverse6: IntWrapper0 = i_w6_w0.get(i)

  @Benchmark def modify0(): IntWrapper0 = w0_i.modify(_ + 1)(w0)
  @Benchmark def modify6(): IntWrapper0 = w0_w3_i.modify(_ + 1)(w0)
  @Benchmark def modify3(): IntWrapper0 = w0_w6_i.modify(_ + 1)(w0)

}

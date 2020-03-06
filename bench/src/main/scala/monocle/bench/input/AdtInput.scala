package monocle.bench.input

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Scope, Setup, State}

@State(Scope.Thread)
class ADTInput extends InputHelper {
  var adt: ADT = _

  private def genADT(): ADT =
    if (genBool()) I(genInt())
    else R(genADT())

  @Setup
  def setup(): Unit =
    adt = genADT()
}

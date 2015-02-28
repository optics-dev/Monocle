package monocle.bench.input

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Scope, Setup, State}

@State(Scope.Thread)
class Nested0Input extends InputHelper {

  var n0: Nested0 = _

  @Setup
  def setup(): Unit =
    n0 = Nested0(genStr(), genInt(),
      Nested1(genStr(), genInt(),
        Nested2(genStr(), genInt(),
          Nested3(genStr(), genInt(),
            Nested4(genStr(), genInt(),
              Nested5(genStr(), genInt(),
                Nested6(genStr(), genInt())
                ,genLong()), genLong()), genLong()), genLong()), genLong()), genLong())

}

package monocle.bench

import java.util.concurrent.TimeUnit
import monocle.Iso
import monocle.macros.GenIso
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(5)
@State(Scope.Benchmark)
class ProductIsoBench {
  case class Mono2(a: Long, b: String)
  case class Mono4(a: Long, b: String, c: Long, d: String)
  case class Mono8(a: Long, b: String, c: Long, d: String, e: Long, f: String, g: Long, h: String)
  case class Poly2[A](a: A, b: List[A])
  case class Poly4[A](a: A, b: List[A], c: A, d: List[A])
  case class Poly8[A](a: A, b: List[A], c: A, d: List[A], e: A, f: List[A], g: A, h: List[A])

  trait Isos {
    val mono2: Iso[Mono2, (Long, String)]
    val mono4: Iso[Mono4, (Long, String, Long, String)]
    val mono8: Iso[Mono8, (Long, String, Long, String, Long, String, Long, String)]
    final type A = Int
    val poly2: Iso[Poly2[A], (A, List[A])]
    val poly4: Iso[Poly4[A], (A, List[A], A, List[A])]
    val poly8: Iso[Poly8[A], (A, List[A], A, List[A], A, List[A], A, List[A])]
  }
  object GenIsoIsos extends Isos {
    override val mono2 = GenIso.fields[Mono2]
    override val mono4 = GenIso.fields[Mono4]
    override val mono8 = GenIso.fields[Mono8]
    override val poly2 = GenIso.fields[Poly2[A]]
    override val poly4 = GenIso.fields[Poly4[A]]
    override val poly8 = GenIso.fields[Poly8[A]]
  }

  trait Test {
    type A
    type B
    val a: A
    val b: B
    val iso: Iso[A, B]
  }
  def Test[X, Y](i: Iso[X, Y])(y: Y): Test =
    new Test {
      override type A = X
      override type B = Y
      override val a   = i reverseGet y
      override val b   = y
      override val iso = i
    }

  @Param(Array("mono2", "mono4", "mono8", "poly2", "poly4", "poly8"))
  var pSubject = ""

  @Param(Array("fields", "shapeless"))
  var pIsos = ""

  var _test: Test = _

  @Setup
  def setup(): Unit = {
    val isos = pIsos match {
      case "fields" => GenIsoIsos
    }
    val a  = 7
    val as = List(3, 5, 7)
    val l  = 9L
    val s  = "asd"
    _test = pSubject match {
      case "mono2" => Test(isos.mono2)((l, s))
      case "mono4" => Test(isos.mono4)((l, s, l, s))
      case "mono8" => Test(isos.mono8)((l, s, l, s, l, s, l, s))
      case "poly2" => Test(isos.poly2)((a, as))
      case "poly4" => Test(isos.poly4)((a, as, a, as))
      case "poly8" => Test(isos.poly8)((a, as, a, as, a, as, a, as))
    }
  }

  // PDTs require the `test` term to be stable, where as this._test is a var.
  def test[A](f: Test => A): A = f(_test)

  @Benchmark def get        = test(t => t.iso get t.a)
  @Benchmark def reverseGet = test(t => t.iso reverseGet t.b)
}

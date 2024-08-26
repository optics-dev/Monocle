package monocle.function

import monocle.MonocleSuite
import monocle.function.Plated
import cats.implicits._
import cats.Applicative
import monocle.Traversal
class PlatedSpec extends MonocleSuite {

  sealed trait Expr

  case class Val(v: Int) extends Expr

  case class Add(lhs: Expr, rhs: Expr) extends Expr

  implicit def exprPlated: Plated[Expr] = new Plated[Expr] {
    def plate: Traversal[Expr, Expr] = new Traversal[Expr, Expr] {
      def modifyA[F[_]: Applicative](f: Expr => F[Expr])(s: Expr): F[Expr] = s match {
        case Add(lhs, rhs) => f(lhs).map2(f(rhs))(Add.apply)
        case _             => s.pure[F]
      }
    }
  }

  def negate(e: Expr): Expr = e match {
    case Val(v) => Val(-v)
    case _      => e
  }

  def add(e: Expr): Option[Expr] = e match {
    case Add(Val(v1), Val(v2)) => Some(Val(v1 + v2))
    case _                     => None
  }

  private val negateAll = Plated.transform(negate)

  private val addAll = Plated.rewrite(add)

  private val universe = Plated.universe[Expr]

  test("apply rule everywhere until that rule cannot be applied anywhere") {
    val e = Add(Add(Val(1), Val(2)), Val(3))
    assert(universe(addAll(e)).forall(add(_).isEmpty))
  }

  test("transform every nested elements") {
    val e = Add(Add(Val(1), Val(1)), Val(1))
    assert(universe(negateAll(e)).collect { case v: Val => v }.forall(_.v < 0))
  }

  test("transform top level leaf node") {
    val e = Val(1)
    assertEquals(negateAll(e), negate(e))
  }

}

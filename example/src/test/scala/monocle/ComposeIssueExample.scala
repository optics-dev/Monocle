package monocle

import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped

// we had to replace compose by non overloaded versions: composeLens, composePrism for the following reason
class ComposeIssueExample extends Spec {

  class A[S, T] {
    def compose[U](a: A[T, U]): A[S, U] = new A[S, U]
    def compose[U](b: B[T, U]): B[S, U] = new B[S, U]
    // non overloaded method
    def composeB[U](b: B[T, U]): B[S, U] = new B[S, U]
  }

  class B[S, T] {
    def compose[U](a: A[T, U]): B[S, U] = new B[S, U]
    def compose[U](b: B[T, U]): B[S, U] = new B[S, U]
  }

  val aI2S = new A[Int, String]
  val aS2S = new A[String, String]

  val bI2S = new B[Int, String]
  val bS2S = new B[String, String]


  "compose same class" in {
    (aI2S compose aS2S).isInstanceOf[A[_, _]] shouldEqual true
    (aS2S compose aS2S).isInstanceOf[A[_, _]] shouldEqual true
    (bI2S compose bS2S).isInstanceOf[B[_, _]] shouldEqual true
  }

  "compose different class" in {
    (aI2S compose bS2S).isInstanceOf[B[_, _]] shouldEqual true
    (bI2S compose aS2S).isInstanceOf[B[_, _]] shouldEqual true
  }

  def b2S[T] = new B[T, String]

  "compose with parametric method" in {
    // explicit type
    (aI2S compose b2S[String]).isInstanceOf[B[_, _]] shouldEqual true
    // non overloaded method
    (aI2S composeB b2S).isInstanceOf[B[_, _]] shouldEqual true

    // do not compile if we do not specify the type with an overloaded method
    // see https://stackoverflow.com/questions/7845569/scala-type-inference-on-overloaded-method/7847406#7847406
    new AnyValueAsResult[Unit].asResult(illTyped("""
      aI2S compose b2S
     """))
  }

}

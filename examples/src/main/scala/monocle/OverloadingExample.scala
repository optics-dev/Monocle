package monocle

trait A {
  val i: Int
  def foo(a: A):A = a

  override def toString: String = s"A: i=$i"
}

trait B extends A {
  def foo(b: B): B = b
  override def toString: String = s"B: i=$i"
}

object OverloadingExample extends App {

  val a1 = new A { val i = 1 }
  val a2 = new A { val i = 2 }

  val b1 = new B { val i = 3 }
  val b2 = new B { val i = 4 }

  println( a1.foo(a2) )
  println( b1.foo(b2) )
  println( a1.foo(b1) )
  println( b1.foo(a1) )




}

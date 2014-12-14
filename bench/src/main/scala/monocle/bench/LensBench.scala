package monocle.bench

import monocle.SimpleLens
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class LensBench {
  case class Person(name: String, age: Int)

  val _name = SimpleLens[Person, String](_.name, (c, n) => c.copy(name = n))
  val _age  = SimpleLens[Person, Int](_.age, (c, h) => c.copy(age = h))

  val john = Person("John", 30)

  @Benchmark def stdGet()  = john.name
  @Benchmark def lensGet() = _name.get(john)

  @Benchmark def directSet() = john.copy(name = "Robert")
  @Benchmark def lensSet()   = _name.setF("Robert")(john)

  @Benchmark def directModify() = john.copy(age = john.age + 1)
  @Benchmark def lensModify()   = _age.modifyF(_ + 1)(john)


  case class Nested1(s: String, i: Int, n: Nested2, l: Long)
  case class Nested2(s: String, i: Int, n: Nested3, l: Long)
  case class Nested3(s: String, i: Int, n: Nested4, l: Long)
  case class Nested4(s: String, i: Int, l: Long)

  val _n2 = SimpleLens[Nested1, Nested2](_.n, (n1, n2) => n1.copy(n = n2))
  val _n3 = SimpleLens[Nested2, Nested3](_.n, (n2, n3) => n2.copy(n = n3))
  val _n4 = SimpleLens[Nested3, Nested4](_.n, (n3, n4) => n3.copy(n = n4))
  val _i  = SimpleLens[Nested4, Int](_.i, (n4, i) => n4.copy(i = i))

  val n1 = Nested1("plop", 45, Nested2("Hello", -678, Nested3("World", 0, Nested4("Yoooo", 42, 12345L), 123456789L), 342072347L), 6789L)

  val n1ToI = _n2 composeLens _n3 composeLens _n4 composeLens _i

  @Benchmark def stdNestedGet()  = n1.n.n.n.i
  @Benchmark def lensNestedGet() = n1ToI.get(n1)

  @Benchmark def stdNestedSet()  = n1.copy(n = n1.n.copy(n = n1.n.n.copy(n = n1.n.n.n.copy(i = 43))))
  @Benchmark def lensNestedSet() = n1ToI.setF(43)(n1)

  @Benchmark def stdNestedModify()  = n1.copy(n = n1.n.copy(n = n1.n.n.copy(n = n1.n.n.n.copy(i = n1.n.n.n.i + 1))))
  @Benchmark def lensNestedModify() = n1ToI.modifyF(_ + 1)(n1)

  def safeDivide(a: Int, b: Int): Maybe[Int] = if(b == 0) Maybe.empty else Maybe.just(a / b)

  @Benchmark def stdModifyF()  = safeDivide(n1.i, 2).map(newI => n1.copy(i = newI))
  @Benchmark def lensModifyF() = n1ToI.lift(n1, safeDivide(_, 2))

}
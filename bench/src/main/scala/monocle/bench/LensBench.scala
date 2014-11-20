package monocle.bench

import monocle.Lens
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class LensBench {
  case class Person(name: String, age: Int)

  val _name = Lens((_: Person).name)( (n, c) => c.copy(name = n))
  val _age  = Lens((_: Person).age )( (h, c) => c.copy(age = h))

  val john = Person("John", 30)

  @Benchmark def stdGet()  = john.name
  @Benchmark def lensGet() = _name.get(john)

  @Benchmark def stdSet()  = john.copy(name = "Robert")
  @Benchmark def lensSet() = _name.set("Robert")(john)

  @Benchmark def stdModify()  = john.copy(age = john.age + 1)
  @Benchmark def lensModify() = _age.modify(_ + 1)(john)


  case class Nested1(s: String, i: Int, n: Nested2, l: Long)
  case class Nested2(s: String, i: Int, n: Nested3, l: Long)
  case class Nested3(s: String, i: Int, n: Nested4, l: Long)
  case class Nested4(s: String, i: Int, l: Long)

  val _n2 = Lens[Nested1, Nested2](_.n)( (n2, n1) => n1.copy(n = n2))
  val _n3 = Lens[Nested2, Nested3](_.n)( (n3, n2) => n2.copy(n = n3))
  val _n4 = Lens[Nested3, Nested4](_.n)( (n4, n3) => n3.copy(n = n4))
  val _i  = Lens[Nested4, Int](_.i)( (i, n4) => n4.copy(i = i))

  val n1 = Nested1("plop", 45, Nested2("Hello", -678, Nested3("World", 0, Nested4("Yoooo", 42, 12345L), 123456789L), 342072347L), 6789L)

  val n1ToI = _n2 composeLens _n3 composeLens _n4 composeLens _i

  @Benchmark def stdNestedGet()  = n1.n.n.n.i
  @Benchmark def lensNestedGet() = n1ToI.get(n1)

  @Benchmark def stdNestedSet()  = n1.copy(n = n1.n.copy(n = n1.n.n.copy(n = n1.n.n.n.copy(i = 43))))
  @Benchmark def lensNestedSet() = n1ToI.set(43)(n1)

  @Benchmark def stdNestedModify()  = n1.copy(n = n1.n.copy(n = n1.n.n.copy(n = n1.n.n.n.copy(i = n1.n.n.n.i + 1))))
  @Benchmark def lensNestedModify() = n1ToI.modify(_ + 1)(n1)

  def safeDivide(a: Int, b: Int): Maybe[Int] = if(b == 0) Maybe.empty else Maybe.just(a / b)

  @Benchmark def stdModifyF()  = safeDivide(n1.i, 2).map(newI => n1.copy(i = newI))
  @Benchmark def lensModifyF() = n1ToI.modifyF(safeDivide(_, 2))(n1)

}

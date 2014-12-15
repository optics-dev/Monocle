package monocle.bench

import monocle.Lens
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.Maybe

@State(Scope.Benchmark)
class LensBench {
  case class Person(name: String, age: Int)

  val _name = Lens((_: Person).name)(n => c => c.copy(name = n))
  val _age  = Lens((_: Person).age )(h => c => c.copy(age = h))

  val john = Person("John", 30)


  def safeDivide(a: Int, b: Int): Maybe[Int] = if(b == 0) Maybe.empty else Maybe.just(a / b)

  @Benchmark def stdGet()  = john.name
  @Benchmark def lensGet() = _name.get(john)

  @Benchmark def stdSet()  = john.copy(name = "Robert")
  @Benchmark def lensSet() = _name.set("Robert")(john)

  @Benchmark def stdModify()  = john.copy(age = john.age + 1)
  @Benchmark def lensModify() = _age.modify(_ + 1)(john)

  @Benchmark def stdModifyF()  = safeDivide(john.age, 2).map(newAge => john.copy(age = newAge))
  @Benchmark def lensModifyF() = _age.modifyF(safeDivide(_, 2))(john)


  case class Nested1(s: String, i: Int, n: Nested2, l: Long)
  case class Nested2(s: String, i: Int, n: Nested3, l: Long)
  case class Nested3(s: String, i: Int, n: Nested4, l: Long)
  case class Nested4(s: String, i: Int, n: Nested5, l: Long)
  case class Nested5(s: String, i: Int, n: Nested6, l: Long)
  case class Nested6(s: String, i: Int, n: Nested7, l: Long)
  case class Nested7(s: String, i: Int)

  val _n2 = Lens[Nested1, Nested2](_.n)(n2 => n1 => n1.copy(n = n2))
  val _n3 = Lens[Nested2, Nested3](_.n)(n3 => n2 => n2.copy(n = n3))
  val _n4 = Lens[Nested3, Nested4](_.n)(n4 => n3 => n3.copy(n = n4))
  val _n5 = Lens[Nested4, Nested5](_.n)(n5 => n4 => n4.copy(n = n5))
  val _n6 = Lens[Nested5, Nested6](_.n)(n6 => n5 => n5.copy(n = n6))
  val _n7 = Lens[Nested6, Nested7](_.n)(n7 => n6 => n6.copy(n = n7))
  
  val _n4_i  = Lens[Nested4, Int](_.i)(i => n4 => n4.copy(i = i))
  val _n7_i  = Lens[Nested7, Int](_.i)(i => n7 => n7.copy(i = i))

  val n1 = Nested1("plop", 45, 
    Nested2("Hello", -678, 
      Nested3("World", 0, 
        Nested4("Yoooo", 42,
          Nested5("Yo", 9999,
            Nested6("WoooooooooooooooW", 76,
              Nested7("", 0)
                ,0), -990993L), 12345L), 123456789L), 342072347L), 6789L)

  val n1Ton4I = _n2 composeLens _n3 composeLens _n4 composeLens _n4_i

  @Benchmark def stdNested3Get()  = n1.n.n.n.i
  @Benchmark def lensNested3Get() = n1Ton4I.get(n1)

  @Benchmark def stdNested3Set()  = n1.copy(n = n1.n.copy(n = n1.n.n.copy(n = n1.n.n.n.copy(i = 43))))
  @Benchmark def lensNeste3dSet() = n1Ton4I.set(43)(n1)

  @Benchmark def stdNested3Modify()  = n1.copy(n = n1.n.copy(n = n1.n.n.copy(n = n1.n.n.n.copy(i = n1.n.n.n.i + 1))))
  @Benchmark def lensNested3Modify() = n1Ton4I.modify(_ + 1)(n1)

  @Benchmark def stdNested3ModifyF()  = safeDivide(n1.n.n.n.i, 2).map(newI => n1.copy(n = n1.n.copy(n = n1.n.n.copy(n = n1.n.n.n.copy(i = newI)))))
  @Benchmark def lensNested3ModifyF() = n1Ton4I.modifyF(safeDivide(_, 2))(n1)


  val n1Ton7I = _n2 composeLens _n3 composeLens _n4 composeLens _n5 composeLens _n6 composeLens _n7 composeLens _n7_i

  @Benchmark def stdNested6Get()  = n1.n.n.n.n.n.n.i
  @Benchmark def lensNested6Get() = n1Ton7I.get(n1)

  @Benchmark def stdNested6Set()  = n1.copy(
    n = n1.n.copy(
      n = n1.n.n.copy(
        n = n1.n.n.n.copy(
          n = n1.n.n.n.n.copy(
            n = n1.n.n.n.n.n.copy(
              n = n1.n.n.n.n.n.n.copy(
                i = 43
  )))))))
  @Benchmark def lensNested6dSet() = n1Ton7I.set(43)(n1)

  @Benchmark def stdNested6Modify()  = n1.copy(
    n = n1.n.copy(
      n = n1.n.n.copy(
        n = n1.n.n.n.copy(
          n = n1.n.n.n.n.copy(
            n = n1.n.n.n.n.n.copy(
              n = n1.n.n.n.n.n.n.copy(
                i = n1.n.n.n.n.n.n.i + 1
            )))))))
  @Benchmark def lensNested6Modify() = n1Ton7I.modify(_ + 1)(n1)

  @Benchmark def stdNested6ModifyF()  = safeDivide(n1.n.n.n.n.n.n.i, 2).map(newI => n1.copy(
    n = n1.n.copy(
      n = n1.n.n.copy(
        n = n1.n.n.n.copy(
          n = n1.n.n.n.n.copy(
            n = n1.n.n.n.n.n.copy(
              n = n1.n.n.n.n.n.n.copy(
                i = 43
  ))))))))
  @Benchmark def lensNested6ModifyF() = n1Ton7I.modifyF(safeDivide(_, 2))(n1)

}

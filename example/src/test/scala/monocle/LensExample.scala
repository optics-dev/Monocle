package monocle

import monocle.macros.{GenLens, Lenses}
import monocle.syntax._
import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped

class LensExample extends Spec {
  
  "Modifications through lenses are chainable" in {
    @Lenses case class Point(x: Int, y: Int)
    import Point._

    val update = x.modify(_ + 100) compose y.set(7)
    update(Point(1,2)) ==== Point(101,7)
  }

  "@Lenses is for case classes only" in {
    new AnyValueAsResult[Unit].asResult(
      illTyped("""@Lenses class C""", "Invalid annotation target: must be a case class")
    )
  }

  "Lens for monomorphic case class fields" in {

    @Lenses // this annotation generate lenses in the companion object of Person
    case class Person(name: String, age: Int)

    object CoreSimpleLens {
      val _name = Lens((_: Person).name)(n => c => c.copy(name = n))
      val _age  = Lens((_: Person).age)( h => c => c.copy(age = h))
    }

    object LenserMacro {
      val genLens = GenLens[Person]

      val name = genLens(_.name)
      val age  = genLens(_.age)
    }

    val john = Person("John", 30)
    
    "Lens get an A from an S" in {
      (john applyLens CoreSimpleLens._name get) ==== "John"
      (john applyLens LenserMacro.name get)     ==== "John"
      (john applyLens Person.name get)          ==== "John"
    }

    "Lens set an A in a S" in {
      val changedJohn = Person("John", 45)

      (john applyLens CoreSimpleLens._age set 45) ==== changedJohn
      (john applyLens LenserMacro.age set 45)     ==== changedJohn
      (john applyLens Person.age set 45)          ==== changedJohn
    }

    @Lenses("_") // this generates lenses prefixed with _ in the Cat companion object
    case class Cat(age: Int)

    val alpha = Cat(2)

    "@Lenses takes an optional prefix string" in {
      (alpha applyLens Cat._age get)   ==== 2
      (alpha applyLens Cat._age set 3) ==== Cat(3)
    }

    "Lenses are created as `val`s" in {
      import scala.reflect.runtime.universe._
      val decls: List[Symbol] = implicitly[WeakTypeTag[Person.type]].tpe.declarations.toList
      decls.exists(_.toString == "value name") ==== true
      decls.exists(_.toString == "value age") ==== true
    }

  }

  "Lens for polymorphic case class fields" in {
    @Lenses // this annotation generate lenses in the companion object of Foo
    case class Foo[A,B](q: Map[(A,B),Double], default: Double)

    object CoreSimpleLens {
      def _q[A,B] = Lens((_: Foo[A,B]).q)(q => f => f.copy(q = q))
      def _default[A,B]  = Lens((_: Foo[A,B]).default)(d => f => f.copy(default = d))
    }

    object LenserMacro {
      def genLens[A,B] = GenLens[Foo[A,B]]

      def q[A,B] = genLens[A,B](_.q)
      def default[A,B]  = genLens[A,B](_.default)
    }

    val candyTrade = Foo[Int,Symbol](Map[(Int,Symbol),Double]((0,'Buy) -> -3.0, (12,'Sell) -> 7), 0.0)
    
    "Lens gets an A from an S" in {
      (candyTrade applyLens CoreSimpleLens._default get) ==== 0.0
      (candyTrade applyLens LenserMacro.default get)     ==== 0.0
      (candyTrade applyLens Foo.default get)             ==== 0.0
    }


    "Lens modifies an A in S" in {
      val changedTrade = Foo[Int,Symbol](Map((0,'Buy) -> -2.0, (12,'Sell) -> 7), 0.0)
      import Foo._
      changedTrade ==== changedTrade
      q.modify((_: Map[(Int,Symbol),Double]).updated((0,'Buy), -2.0))(candyTrade) ==== changedTrade
    }

    "Lenses are created as `def`s" in {
      import scala.reflect.runtime.universe._
      val decls: List[Symbol] = implicitly[WeakTypeTag[Foo.type]].tpe.declarations.toList
      decls.exists(_.toString == "method q") ==== true
      decls.exists(_.toString == "method default") ==== true
    }
  }
}

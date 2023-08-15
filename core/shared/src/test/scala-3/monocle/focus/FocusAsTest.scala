package monocle.focus

import monocle.Focus
import monocle.syntax.all.as

import scala.annotation.nowarn

final class FocusAsTest extends munit.FunSuite {

  trait Food { def calories: Int }
  case class Banana(calories: Int, squishy: Boolean)   extends Food
  case class Apple(calories: Int, color: String)       extends Food
  case class MysteryFood[A](mystery: A, calories: Int) extends Food
  case class Meal(mainIngredient: Food)

  test("Cast a broad thing to a narrow thing, directly on the argument") {
    val asBanana = Focus[Food](_.as[Banana])

    val foodA: Food = Apple(33, "red")
    val foodB: Food = Banana(40, true)

    assertEquals(asBanana.getOption(foodB), Some(Banana(40, true)))
    assertEquals(asBanana.getOption(foodA), None)
  }

  test("Cast a broad thing to a narrow thing, nested") {
    val mealAppleColor = Focus[Meal](_.mainIngredient.as[Apple].color)

    val mealA = Meal(Apple(24, "green"))
    val mealB = Meal(Banana(50, false))

    assertEquals(mealAppleColor.getOption(mealA), Some("green"))
    assertEquals(mealAppleColor.getOption(mealB), None)
  }

  test("Cast a broad thing to a narrow thing with type parameters") {
    // Generates warning, but it is allowed
    val getMystery  = Focus[Food](_.as[MysteryFood[String]].mystery)
    val mysteryFood = MysteryFood[String]("abc", 44)

    assertEquals(getMystery.getOption(mysteryFood), Some("abc"))
  }

  test("Focus operator `as` commutes with standalone operator `as`") {
    val asBanana = Focus[Food](_.as[Banana])

    val foodA: Food = Apple(35, "blue")
    val foodB: Food = Banana(-88, false)

    assertEquals(Focus[Food](_.as[Banana]).getOption(foodB), Focus[Food]().as[Banana].getOption(foodB))

    assertEquals(Focus[Food](_.as[Banana]).getOption(foodA), Focus[Food]().as[Banana].getOption(foodA))
  }
}

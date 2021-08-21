
package monocle

final class CatsCompatTest extends munit.FunSuite {

  test("Setter index doesn't conflict with Cats") {
    import cats.syntax.all._
    val setter = Setter.id[List[Int]]
    val composed: Setter[List[Int], Int] = setter.index(5)
  }

  test("Traversal index doesn't conflict with Cats") {
    import cats.syntax.all._
    val traversal = Traversal.id[List[Int]]
    val composed: Traversal[List[Int], Int] = traversal.index(5)
  }

  test("Optional index doesn't conflict with Cats") {
    import cats.syntax.all._
    val optional = Optional.id[List[Int]]
    val composed: Optional[List[Int], Int] = optional.index(5)
  }

  test("Lens index doesn't conflict with Cats") {
    import cats.syntax.all._
    val lens = Lens.id[List[Int]]
    val composed: Optional[List[Int], Int] = lens.index(5)
  }

  test("Prism index doesn't conflict with Cats") {
    import cats.syntax.all._
    val prism = Prism.id[List[Int]]
    val composed: Optional[List[Int], Int] = prism.index(5)
  }

  test("Iso index doesn't conflict with Cats") {
    import cats.syntax.all._
    val iso = Iso.id[List[Int]]
    val composed: Optional[List[Int], Int] = iso.index(5)
  }

  test("Getter index doesn't conflict with Cats") {
    import cats.syntax.all._
    val getter = Getter.id[List[Int]]
    val composed: Fold[List[Int], Int] = getter.index(5)
  }

  test("Fold index doesn't conflict with Cats") {
    import cats.syntax.all._
    val fold = Fold.id[List[Int]]
    val composed: Fold[List[Int], Int] = fold.index(5)
  }
}

package monocle

import monocle.std.either.stdRight
import monocle.function.Index.index

final class CompositionGenericOpticsTest extends munit.FunSuite {

  test("compose with parametric optic") {
    val root = Iso.id[Either[String, Int]]
    compileErrors("root.andThen(stdRight)")
    root.andThen(stdRight[String, Int])
    root.andThen(stdRight: Prism[Either[String, Int], Int])
    root.composePrism(stdRight)
  }

  test("compose with generic optic") {
    val root = Iso.id[List[Int]]
    compileErrors("root.andThen(index(3))")
    root.andThen(index(3): Optional[List[Int], Int])
    root.composeOptional(index(3))
  }

}

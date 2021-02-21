package monocle

final class OverloadTest extends munit.FunSuite {

  test("only andThen is overloaded") {
    val optic = Iso.id

    val whiteListedMethods = Set(
      "andThen",
      "wait",
      "apply",
      "unapply",
      "to",   // double-check why 2
      "adapt" // internal
    )

    val overloaded = optic.getClass.getMethods.toList
      .map(_.getName)
      .filterNot(_.startsWith("$"))
      .filterNot(whiteListedMethods)
      .groupMapReduce(identity)(_ => 1)(_ + _)
      .filter(_._2 > 1)

    assertEquals(overloaded, Map.empty[String, Int])
  }

}

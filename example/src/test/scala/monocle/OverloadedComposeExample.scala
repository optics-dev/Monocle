package monocle


import monocle.function.Each._
import monocle.std.option._
import org.specs2.scalaz.Spec
import shapeless.test.illTyped
import org.specs2.execute.{AnyValueAsResult, AsResult}

/**
 * Illustrate the purpose of specific compose function for each main concept (Lens, Traversal, etc)
 */
class OverloadedComposeExample extends Spec {

  case class Example(_opt: Option[Int])

  val optLens = Macro.mkLens[Example, Option[Int]]("_opt")
  val example = Example(Some(2))

  "compose does not compile between Lens and Prism" in {
    new AnyValueAsResult[Unit].asResult(illTyped("""
      optLens.compose(some).getAll(example)
    """))
  }

  "but composeTraversal does" in {
    optLens.composeTraversal(some).getAll(example) shouldEqual List(2)
  }

  "compose and implicit do not work together" in {
    new AnyValueAsResult[Unit].asResult(illTyped("""
      optLens.compose(each).getAll(example)
    """))
  }

  "but composeTraversal is fine" in {
    optLens.composeTraversal(each).getAll(example) shouldEqual List(2)
  }


}

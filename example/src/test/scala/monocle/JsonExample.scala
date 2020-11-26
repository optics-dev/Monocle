package monocle

import alleycats.std.all._
import monocle.function.Plated
import monocle.unsafe.MapTraversal._

/** Show how could we use Optics to manipulate some Json AST
  */
class JsonExample extends MonocleSuite {
  sealed trait Json

  case class JsString(s: String)            extends Json
  case class JsNumber(n: Int)               extends Json
  case class JsArray(l: List[Json])         extends Json
  case class JsObject(m: Map[String, Json]) extends Json

  val jsString = Prism[Json, String] { case JsString(s) => Some(s); case _ => None }(JsString.apply)
  val jsNumber = Prism[Json, Int] { case JsNumber(n) => Some(n); case _ => None }(JsNumber.apply)
  val jsArray  = Prism[Json, List[Json]] { case JsArray(a) => Some(a); case _ => None }(JsArray.apply)
  val jsObject = Prism[Json, Map[String, Json]] { case JsObject(m) => Some(m); case _ => None }(JsObject.apply)

  val json: Json = JsObject(
    Map(
      "first_name" -> JsString("John"),
      "last_name"  -> JsString("Doe"),
      "age"        -> JsNumber(28),
      "siblings" -> JsArray(
        List(
          JsObject(
            Map(
              "first_name" -> JsString("Elia"),
              "age"        -> JsNumber(23)
            )
          ),
          JsObject(
            Map(
              "first_name" -> JsString("Robert"),
              "age"        -> JsNumber(25)
            )
          )
        )
      )
    )
  )

  test("Json Prism") {
    assertEquals(jsNumber.getOption(JsString("plop")), None)
    assertEquals(jsNumber.getOption(JsNumber(2)), Some(2))
  }

  test("Use index to go into an JsObject or JsArray") {
    assertEquals(jsObject.composeOptional(index("age")).andThen(jsNumber).getOption(json), Some(28))

    assertEquals(
      jsObject
        .composeOptional(index("siblings"))
        .andThen(jsArray)
        .composeOptional(index(1))
        .andThen(jsObject)
        .composeOptional(index("first_name"))
        .andThen(jsString)
        .set("Robert Jr.")(json),
      JsObject(
        Map(
          "first_name" -> JsString("John"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(28),
          "siblings" -> JsArray(
            List(
              JsObject(
                Map(
                  "first_name" -> JsString("Elia"),
                  "age"        -> JsNumber(23)
                )
              ),
              JsObject(
                Map(
                  "first_name" -> JsString("Robert Jr."), // name is updated
                  "age"        -> JsNumber(25)
                )
              )
            )
          )
        )
      )
    )
  }

  test("Use at to add delete fields") {
    assertEquals(
      jsObject.composeLens(at("nick_name")).set(Some(JsString("Jojo")))(json),
      JsObject(
        Map(
          "first_name" -> JsString("John"),
          "nick_name"  -> JsString("Jojo"), // new field
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(28),
          "siblings" -> JsArray(
            List(
              JsObject(
                Map(
                  "first_name" -> JsString("Elia"),
                  "age"        -> JsNumber(23)
                )
              ),
              JsObject(
                Map(
                  "first_name" -> JsString("Robert"),
                  "age"        -> JsNumber(25)
                )
              )
            )
          )
        )
      )
    )

    assertEquals(
      jsObject.composeLens(at("age")).set(None)(json),
      JsObject(
        Map(
          "first_name" -> JsString("John"),
          "last_name"  -> JsString("Doe"), // John is ageless now
          "siblings" -> JsArray(
            List(
              JsObject(
                Map(
                  "first_name" -> JsString("Elia"),
                  "age"        -> JsNumber(23)
                )
              ),
              JsObject(
                Map(
                  "first_name" -> JsString("Robert"),
                  "age"        -> JsNumber(25)
                )
              )
            )
          )
        )
      )
    )
  }

  test("Use each and filterIndex to modify several fields at a time") {
    assertEquals(
      jsObject
        .composeTraversal(filterIndex((_: String).contains("name")))
        .andThen(jsString)
        .composeOptional(headOption)
        .modify(_.toLower)(json),
      JsObject(
        Map(
          "first_name" -> JsString("john"), // starts with lower case
          "last_name"  -> JsString("doe"), // starts with lower case
          "age"        -> JsNumber(28),
          "siblings" -> JsArray(
            List(
              JsObject(
                Map(
                  "first_name" -> JsString("Elia"),
                  "age"        -> JsNumber(23)
                )
              ),
              JsObject(
                Map(
                  "first_name" -> JsString("Robert"),
                  "age"        -> JsNumber(25)
                )
              )
            )
          )
        )
      )
    )

    assertEquals(
      jsObject
        .composeOptional(index("siblings"))
        .andThen(jsArray)
        .composeTraversal(each)
        .andThen(jsObject)
        .composeOptional(index("age"))
        .andThen(jsNumber)
        .modify(_ + 1)(json),
      JsObject(
        Map(
          "first_name" -> JsString("John"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(28),
          "siblings" -> JsArray(
            List(
              JsObject(
                Map(
                  "first_name" -> JsString("Elia"),
                  "age"        -> JsNumber(24) // Elia is older
                )
              ),
              JsObject(
                Map(
                  "first_name" -> JsString("Robert"),
                  "age"        -> JsNumber(26) // Robert is older
                )
              )
            )
          )
        )
      )
    )
  }

  implicit val jsonPlated: Plated[Json] = new Plated[Json] {
    import cats.Applicative
    import cats.Traverse
    import cats.syntax.functor._
    import cats.syntax.traverse._

    val plate: Traversal[Json, Json] = new Traversal[Json, Json] {
      def modifyF[F[_]: Applicative](f: Json => F[Json])(a: Json): F[Json] =
        a match {
          case j @ (JsString(_) | JsNumber(_)) => Applicative[F].pure(j)
          case JsArray(l)                      => l.traverse(f).map(JsArray)
          case JsObject(m)                     => Traverse[Map[String, *]].traverse(m)(f).map(JsObject)
        }
    }
  }

  test("Plated instance to rewrite any matching elements") {
    assertEquals(
      Plated.rewrite[Json] {
        case JsString(s) =>
          val u = s.toUpperCase
          if (s != u) Some(JsString(u)) else None
        case _ => None
      }(json),
      JsObject(
        Map(
          "first_name" -> JsString("JOHN"),
          "last_name"  -> JsString("DOE"),
          "age"        -> JsNumber(28),
          "siblings" -> JsArray(
            List(
              JsObject(
                Map(
                  "first_name" -> JsString("ELIA"),
                  "age"        -> JsNumber(23)
                )
              ),
              JsObject(
                Map(
                  "first_name" -> JsString("ROBERT"),
                  "age"        -> JsNumber(25)
                )
              )
            )
          )
        )
      )
    )
  }
}

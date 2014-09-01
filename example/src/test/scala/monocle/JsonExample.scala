package monocle

import org.specs2.scalaz.Spec

import monocle.function._
import monocle.std._

import scalaz.Maybe

/**
 * Show how could we use Optics to manipulate some Json AST
 */
class JsonExample extends Spec {

  sealed trait Json

  case class JsString(s: String) extends Json
  case class JsNumber(n: Int) extends Json
  case class JsArray(l: List[Json]) extends Json
  case class JsObject(m: Map[String, Json]) extends Json

  val jsString = SimplePrism[Json, String]({ case JsString(s) => Maybe.just(s); case _ => Maybe.empty}, JsString.apply)
  val jsNumber = SimplePrism[Json, Int]({ case JsNumber(n) => Maybe.just(n); case _ => Maybe.empty}, JsNumber.apply)
  val jsArray  = SimplePrism[Json, List[Json]]({ case JsArray(a) => Maybe.just(a); case _ => Maybe.empty}, JsArray.apply)
  val jsObject = SimplePrism[Json, Map[String, Json]]({ case JsObject(m) => Maybe.just(m); case _ => Maybe.empty}, JsObject.apply)

  val json = JsObject(Map(
    "first_name" -> JsString("John"),
    "last_name"  -> JsString("Doe"),
    "age"        -> JsNumber(28),
    "siblings"   -> JsArray(List(
      JsObject(Map(
        "first_name" -> JsString("Elia"),
        "age"        -> JsNumber(23)
      )),
      JsObject(Map(
        "first_name" -> JsString("Robert"),
        "age"        -> JsNumber(25)
      ))
    ))
  ))

  "Json Prism" in {
    jsNumber.getMaybe(JsString("plop")) ==== Maybe.empty
    jsNumber.getMaybe(JsNumber(2))      ==== Maybe.just(2)
  }

  "Use index to go into an JsObject or JsArray" in {
    (jsObject composeOptional index("age") composePrism jsNumber).getMaybe(json) ==== Maybe.just(28)

    (jsObject composeOptional index("siblings")
              composePrism    jsArray
              composeOptional index(1)
              composePrism    jsObject
              composeOptional index("first_name")
              composePrism    jsString
    ).set("Robert Jr.")(json) ==== JsObject(Map(
      "first_name" -> JsString("John"),
      "last_name"  -> JsString("Doe"),
      "age"        -> JsNumber(28),
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Elia"),
          "age"        -> JsNumber(23)
        )),
        JsObject(Map(
          "first_name" -> JsString("Robert Jr."), // name is updated
          "age"        -> JsNumber(25)
        ))
      ))
    ))
  }

  "Use at to add delete fields" in {
    (jsObject composeLens at("nick_name")).set(Maybe.just(JsString("Jojo")))(json) ==== JsObject(Map(
      "first_name" -> JsString("John"),
      "nick_name"  -> JsString("Jojo"), // new field
      "last_name"  -> JsString("Doe"),
      "age"        -> JsNumber(28),
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Elia"),
          "age"        -> JsNumber(23)
        )),
        JsObject(Map(
          "first_name" -> JsString("Robert"),
          "age"        -> JsNumber(25)
        ))
      ))
    ))

    (jsObject composeLens at("age")).set(Maybe.empty)(json) ==== JsObject(Map(
      "first_name" -> JsString("John"),
      "last_name"  -> JsString("Doe"), // John is ageless now
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Elia"),
          "age"        -> JsNumber(23)
        )),
        JsObject(Map(
          "first_name" -> JsString("Robert"),
          "age"        -> JsNumber(25)
        ))
      ))
    ))
  }

  "Use each and filterIndex to modify several fields at a time" in {
    (jsObject composeTraversal filterIndex((_: String).contains("name"))
              composePrism     jsString
              composeOptional  headMaybe
    ).modify(_.toLower)(json) ==== JsObject(Map(
      "first_name" -> JsString("john"), // starts with lower case
      "last_name"  -> JsString("doe"),  // starts with lower case
      "age"        -> JsNumber(28),
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Elia"),
          "age"        -> JsNumber(23)
        )),
        JsObject(Map(
          "first_name" -> JsString("Robert"),
          "age"        -> JsNumber(25)
        ))
      ))
    ))


    (jsObject composeOptional  index("siblings")
              composePrism     jsArray
              composeTraversal each
              composePrism     jsObject
              composeOptional  index("age")
              composePrism     jsNumber
    ).modify(_ + 1)(json) ==== JsObject(Map(
      "first_name" -> JsString("John"),
      "last_name"  -> JsString("Doe"),
      "age"        -> JsNumber(28),
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Elia"),
          "age"        -> JsNumber(24)    // Elia is older
        )),
        JsObject(Map(
          "first_name" -> JsString("Robert"),
          "age"        -> JsNumber(26)    // Robert is older
        ))
      ))
    ))
  }

}

package monocle

import monocle.macros.{GenIso, GenLens, GenPrism}

/** Show how could we use Monocle to handle custom case classes, objects
  */
class HttpRequestExample extends MonocleSuite {
  import HttpRequestExample._

  val r1 = HttpRequest(
    GET,
    URI("localhost", 8080, "/ping", Map("hop" -> "5")),
    Map("socket_timeout" -> "20", "connection_timeout" -> "10"),
    ""
  )

  val r2 = HttpRequest(POST, URI("gooogle.com", 443, "/search", Map("keyword" -> "monocle")), Map.empty, "")

  val method  = GenLens[HttpRequest](_.method)
  val uri     = GenLens[HttpRequest](_.uri)
  val headers = GenLens[HttpRequest](_.headers)
  val body    = GenLens[HttpRequest](_.body)

  val host  = GenLens[URI](_.host)
  val query = GenLens[URI](_.query)

  val get: Prism[HttpMethod, Unit] = GenPrism[HttpMethod, GET.type].andThen(GenIso.unit[GET.type])
  val post                         = GenPrism[HttpMethod, POST.type].andThen(GenIso.unit[POST.type])

  test("get and post") {
    assertEquals(method.andThen(get).nonEmpty(r1), true)
    assertEquals(method.andThen(post).nonEmpty(r1), false)
    assertEquals(method.andThen(post).getOption(r2), Some(()))
  }

  test("host") {
    assertEquals(uri.andThen(host).replace("google.com")(r2), r2.copy(uri = r2.uri.copy(host = "google.com")))
  }

  test("query using index") {
    val r = uri.andThen(query).index("hop").andThen(stringToInt).modify(_ + 10)(r1)

    assertEquals(r.uri.query.get("hop"), Some("15"))
  }

  test("query using at") {

    /**  `at` returns Lens[S, Option[A]] while `index` returns Optional[S, A]
      *  So that we need the `some: Prism[Option[A], A]` for further investigation
      */
    val r = uri.andThen(query).at("hop").some.andThen(stringToInt).modify(_ + 10)(r1)

    assertEquals(r.uri.query.get("hop"), Some("15"))
  }

  test("headers") {
    val r = headers.at("Content-Type").replace(Some("text/plain; utf-8"))(r2)
    assertEquals(r.headers.get("Content-Type"), Some("text/plain; utf-8"))
  }

  test("headers with filterIndex") {
    val r = headers
      .filterIndex { h: String => h.contains("timeout") }
      .andThen(stringToInt)
      .modify(_ * 2)(r1)

    assertEquals(r.headers.get("socket_timeout"), Some("40"))
    assertEquals(r.headers.get("connection_timeout"), Some("20"))
  }
}

object HttpRequestExample {
  sealed trait HttpMethod
  case object GET  extends HttpMethod
  case object POST extends HttpMethod

  case class URI(host: String, port: Int, path: String, query: Map[String, String])
  case class HttpRequest(method: HttpMethod, uri: URI, headers: Map[String, String], body: String)
}

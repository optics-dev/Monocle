package monocle.std

import java.net.URL
import monocle.Prism
import scala.util.Try

private[std] trait PlatformSpecificStringOptics {
  val stringToURL: Prism[String, URL] =
    Prism { s: String =>
      Try(new URL(s)).toOption
    }(_.toString)
}

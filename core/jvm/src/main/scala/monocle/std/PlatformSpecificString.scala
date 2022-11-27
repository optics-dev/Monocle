package monocle.std

import java.net.{URI, URL}
import monocle.Prism
import scala.util.Try

private[std] trait PlatformSpecificStringOptics {
  val stringToURI: Prism[String, URI] =
    Prism((s: String) => Try(new URI(s)).toOption)(_.toString)
  
  /** Warning: `java.net.URL` is dangerous, prefer using `URI` with `stringToURI`. */
  val stringToURL: Prism[String, URL] =
    Prism((s: String) => Try(new URL(s)).toOption)(_.toString)
}

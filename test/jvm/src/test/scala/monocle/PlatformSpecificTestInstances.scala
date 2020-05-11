package monocle

import java.net.URL
import org.scalacheck.{Arbitrary, Cogen, Gen}
import cats.Eq

private[monocle] trait PlatformSpecificTestInstances {
  implicit val urlEqual: Eq[URL] = Eq.instance((a, b) => a.toURI() == b.toURI())

  implicit def urlArbitrary: Arbitrary[URL] =
    Arbitrary {
      val idGen = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)

      def path(sep: String): Gen[String] =
        for {
          length <- Gen.choose(0, 5)
          path   <- Gen.listOfN(length, idGen)
        } yield path.mkString(sep, "/", "")

      val url: Gen[URL] = for {
        protocol <- Gen.oneOf("http", "https", "file")
        host     <- idGen
        port     <- Gen.choose(0, 65535)
        path     <- path("/")
      } yield new URL(protocol, host, port, path)

      val jar: Gen[URL] = for {
        url  <- url
        path <- path("!/")
      } yield new URL("jar:" + url + path)

      Gen.frequency((3, url), (1, jar))
    }

  implicit def urlCoGen: Cogen[URL] =
    Cogen[String].contramap[URL](_.toString)
}

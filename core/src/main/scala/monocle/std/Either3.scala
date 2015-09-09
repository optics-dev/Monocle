package monocle.std

import monocle.{Prism, PPrism}
import scalaz.{Either3, Left3, Middle3, Right3}
import scalaz.{-\/, \/-}

object either3 extends Either3Optics

trait Either3Optics {

  final def pLeft3[A, B, C, D]: PPrism[Either3[A, B, C], Either3[D, B, C], A, D] =
    PPrism[Either3[A, B, C], Either3[D, B, C], A, D] {
      case Left3(a)   => \/-(a)
      case Middle3(b) => -\/(Middle3(b))
      case Right3(c)  => -\/(Right3(c))
    }(Left3.apply)

  final def left3[A, B, C]: Prism[Either3[A, B, C], A] =
    pLeft3[A, B, C, A]

  final def pMiddle3[A, B, C, D]: PPrism[Either3[A, B, C], Either3[A, D, C], B, D] =
    PPrism[Either3[A, B, C], Either3[A, D, C], B, D] {
      case Left3(a)   => -\/(Left3(a))
      case Middle3(b) => \/-(b)
      case Right3(c)  => -\/(Right3(c))
    }(Middle3.apply)

  final def middle3[A, B, C]: Prism[Either3[A, B, C], B] =
    pMiddle3[A, B, C, B]

  final def pRight3[A, B, C, D]: PPrism[Either3[A, B, C], Either3[A, B, D], C, D] =
    PPrism[Either3[A, B, C], Either3[A, B, D], C, D] {
      case Left3(a)   => -\/(Left3(a))
      case Middle3(b) => -\/(Middle3(b))
      case Right3(c)  => \/-(c)
    }(Right3.apply)

  final def right3[A, B, C]: Prism[Either3[A, B, C], C] =
    pRight3[A, B, C, C]
}

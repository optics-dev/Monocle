package monocle.function

import monocle.Prism

trait SnocInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamSnoc[A]: Snoc[Stream[A], A] =
    Snoc(
      Prism[Stream[A], (Stream[A], A)](s =>
        for {
          init <- if (s.isEmpty) None else Some(s.init)
          last <- s.lastOption
        } yield (init, last)
      ) {
        case (init, last) => init :+ last
      }
    )

}

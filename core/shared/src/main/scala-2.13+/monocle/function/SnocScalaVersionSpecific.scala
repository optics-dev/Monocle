package monocle.function

import monocle.Prism

trait SnocInstancesScalaVersionSpecific {
  /************************************************************************************************/
  /** 2.13 std instances                                                                          */
  /************************************************************************************************/
  implicit def lazyListSnoc[A]: Snoc[LazyList[A], A] = Snoc(
    Prism[LazyList[A], (LazyList[A], A)](
      s =>
        for {
          init <- if (s.isEmpty) None else Some(s.init)
          last <- s.lastOption
        } yield (init, last)
    ) {
      case (init, last) => init :+ last
    }
  )
}

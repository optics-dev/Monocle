package monocle.function

import monocle.Optional

trait IndexInstancesScalaVersionSpecific {

  /************************************************************************************************/
  /** 2.12 std instances                                                                          */
  /************************************************************************************************/
  implicit def streamIndex[A]: Index[Stream[A], Int, A] = Index(i =>
    if (i < 0)
      Optional[Stream[A], A](_ => None)(_ => identity)
    else
      Optional[Stream[A], A](_.drop(i).headOption)(a => s =>
        s.zipWithIndex.map{ case (value, index) => if(i == index) a else value }
      )
  )
}

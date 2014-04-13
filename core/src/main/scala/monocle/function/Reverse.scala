package monocle.function

import monocle.SimpleIso

trait Reverse[S] {

  /** Creates an Iso from S to a reversed S */
  def reverse: SimpleIso[S, S]

}

object Reverse extends ReverseInstances

trait ReverseInstances {

  def apply[S](_reverse: S => S): Reverse[S] = new Reverse[S] {
    def reverse: SimpleIso[S, S] = SimpleIso[S, S](_reverse, _reverse)
  }

  def reverse[S](implicit ev: Reverse[S]): SimpleIso[S, S] = ev.reverse

  implicit def listReverse[A]  : Reverse[List[A]]   = apply[List[A]](_.reverse)
  implicit def streamReverse[A]: Reverse[Stream[A]] = apply[Stream[A]](_.reverse)
  implicit def stringReverse[A]: Reverse[String]    = apply[String](_.reverse)

}

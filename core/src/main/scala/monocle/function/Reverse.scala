package monocle.function

import monocle.SimpleIso

trait Reverse[S, A] {

  /** Creates an Iso from S to a reversed S */
  def reverse: SimpleIso[S, A]

}

object Reverse extends ReverseFunctions

trait ReverseFunctions {

  def simple[S](_reverse: S => S): Reverse[S, S] = new Reverse[S, S] {
    def reverse: SimpleIso[S, S] = SimpleIso[S, S](_reverse, _reverse)
  }

  def reverse[S, A](implicit ev: Reverse[S, A]): SimpleIso[S, A] = ev.reverse

}

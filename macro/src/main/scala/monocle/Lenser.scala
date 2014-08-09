package monocle

import scala.language.experimental.macros

class Lenser[A] {
  def apply[B](field: A => B): SimpleLens[A, B] = macro MacroImpl.lenser_impl[A, B]
}

object Lenser {
  def apply[A] = new Lenser[A]
}



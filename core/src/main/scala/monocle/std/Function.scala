package monocle.std

import monocle._


object function extends FunctionInstances

trait FunctionInstances {

  def curry[A, B, C]: SimpleIso[(A, B) => C, A => B => C] =
    SimpleIso[(A, B) => C, A => B => C](f => { a: A => b: B => f(a,b) }, g => { (a: A, b: B) => g(a)(b) })

  def uncurry[A, B, C]: SimpleIso[A => B => C, (A, B) => C] = curry.reverse

  def flip[A, B, C]: SimpleIso[A => B => C, B => A => C] =
    SimpleIso[A => B => C, B => A => C](f => { b: B => a: A => f(a)(b) }, g => { a: A => b: B => g(b)(a) })

}

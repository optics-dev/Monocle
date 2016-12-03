package monocle.example

import monocle.Optional

object OptionalExample {

  def nil[A] = Optional[List[A], Unit] {
    case Nil => Some(())
    case _ => None
  }(_ => _ => Nil)

  def head[A] = Optional[List[A], A] {
    case Nil => None
    case x :: _ => Some(x)
  }(h => {
    case Nil => Nil
    case _ :: xs => h :: xs
  })

  def cons[A] = Optional[List[A], List[A]] {
    case Nil => None
    case _ :: xs => Some(xs)
  }(t => {
    case Nil => Nil
    case x :: _ => x :: t
  })

}

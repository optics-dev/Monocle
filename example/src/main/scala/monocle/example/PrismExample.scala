package monocle.example

import monocle.Prism

object PrismExample {

  sealed trait LinkedList[A]
  case class Nil[A]() extends LinkedList[A]
  case class Cons[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

  def _nil[A] = Prism[LinkedList[A], Unit]{
    case Nil()      => Some(())
    case Cons(_, _) => None
  }(_ => Nil())

  def _cons[A] = Prism[LinkedList[A], (A, LinkedList[A])]{
    case Nil()      => None
    case Cons(h, t) => Some((h, t))
  }{ case (h, t) => Cons(h, t)}


}

package monocle.util

import monocle.SimpleTraversal
import scala.annotation.implicitNotFound

trait Each[S] {
  type IN

  def each: SimpleTraversal[S, IN]

}

object Each {

  @implicitNotFound(msg = "Cannot find instance of Each[${S}, ${A}] in scope, typically you want to import " +
    "monocle.std.<CLASS>._, e.g. import monocle.std.list._ to get List Each instance")
  type Aux[S, A] = Each[S] { type IN = A }

}



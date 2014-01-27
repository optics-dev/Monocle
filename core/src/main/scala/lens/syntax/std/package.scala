package lens.syntax

import lens.Traversal
import scalaz.std.list.listInstance
import scalaz.std.option.optionInstance

package object std {

  def option[A]: Traversal[Option[A], A] = Traversal[Option, A]

  def list[A]: Traversal[List[A], A] = Traversal[List, A]

}

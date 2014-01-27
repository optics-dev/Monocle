package lens.syntax

import lens.Traversal
import scalaz.Traverse
import scalaz.std.list.listInstance
import scalaz.std.option.optionInstance

package object std {

  def traverse[T[_]: Traverse, A] = Traversal[T, A]

  def option[A]: Traversal[Option[A], A] = traverse[Option, A]

  def list[A]: Traversal[List[A], A] = traverse[List, A]

}

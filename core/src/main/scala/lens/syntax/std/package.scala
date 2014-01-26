package lens.syntax

import lens.Setter

import scalaz.std.option.optionInstance
import scalaz.std.list.listInstance

package object std {

  def option[A]: Setter[Option[A], A] = Setter[Option, A]

  def list[A]: Setter[List[A], A] = Setter[List, A]

}

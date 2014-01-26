package lens.syntax

import lens.Setter

class AppliedSetter[A, B](from: A, setter: Setter[A, B]){

  def set(newValue: B): A = setter.set(from, newValue)
  def modify(f: B => B): A = setter.modify(from, f)

  def >-[C](other: Setter[B,C]): AppliedSetter[A,C] = new AppliedSetter[A, C](from, setter >- other)

}


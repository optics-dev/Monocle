package lens



trait Lens[A,B] extends Getter[A,B] with Setter[A, B] with Modifier[A, B] {
  // default implementation of set using modify
  def set(from: A, newValue: B): A = modify(from, _ => newValue)

  def >-[C](other: Lens[B,C]): Lens[A,C] = Lens.compose(this, other)
}

trait Getter[A, B]{
  def get(from: A): B
}

trait Setter[A,B] {
  def set(from: A, newValue: B): A
}

trait Modifier[A, B] extends Setter[A, B] {
  def modify(from: A, f: B => B): A
}

object Lens {
  def compose[A, B, C](a2b: Lens[A, B], b2C: Lens[B, C]): Lens[A, C] =  new Lens[A, C] {
    def get(from: A): C = b2C.get(a2b.get(from))

    def modify(from: A, f: C => C): A = a2b.modify(from, b2C.modify(_, f))
  }
}
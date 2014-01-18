package lens

import scalaz.Monoid


trait Traversal[A, B] {

  def get(from: A): List[B]

  def fold(from: A)(implicit ev: Monoid[B]): B = get(from).foldRight(ev.zero)((c1,c2) => ev.append(c1, c2))

  def set(from: A, newValue: B): A = modify(from, _ => newValue)

  def modify(from: A, f: B => B): A

  def >-[C](other: Traversal[B,C]): Traversal[A,C] = Traversal.compose(this, other)
  def >-[C](other: Lens[B,C]): Traversal[A,C]      = Traversal.compose(this, other)

}


object Traversal {
  def compose[A, B, C](a2b: Traversal[A, B], b2C: Traversal[B, C]): Traversal[A, C] = new Traversal[A, C] {

    def get(from: A): List[C] = a2b.get(from) flatMap b2C.get

    def modify(from: A, f: C => C): A = a2b.modify(from, b2C.modify(_, f))
  }

  def compose[A, B, C](a2b: Traversal[A, B], b2C: Lens[B, C]): Traversal[A, C] = new Traversal[A, C] {

    def get(from: A): List[C] = a2b.get(from) map b2C.get

    def modify(from: A, f: C => C): A = a2b.modify(from, b2C.modify(_, f))
  }

  def compose[A, B, C](a2b: Lens[A, B], b2C: Traversal[B, C]): Traversal[A, C] = new Traversal[A, C] {

    def get(from: A): List[C] = b2C.get(a2b.get(from))

    def modify(from: A, f: C => C): A = a2b.modify(from, b2C.modify(_, f))
  }
}

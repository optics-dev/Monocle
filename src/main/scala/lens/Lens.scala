package lens

/**
 * Created by julient on 17/01/2014.
 */
trait Lens[A,B] extends Getter[A,B] with Setter[A, B] {
  def >-[C](other: Lens[B,C]): Lens[A,C] = Lens.compose(this, other)
}

trait Getter[A, B]{
  def get(from: A): B
}

trait Setter[A,B] {
  def set(from: A, newValue: B): A
}

object Lens {
  def compose[A, B, C](a2B: Lens[A, B], b2C: Lens[B, C]): Lens[A, C] =  new Lens[A, C] {
    def get(from: A): C = b2C.get(a2B.get(from))

    def set(from: A, newValue: C): A = {
      val b = a2B.get(from)
      val newB = b2C.set(b, newValue )
      a2B.set(from, newB)
    }
  }
}
package monocle.tuples


trait First[S, A] {
  def get_1(from: S): A
  def set_1(from: S, newValue: A): S
}

trait Second[S, A] {
  def get_2(from: S): A
  def set_2(from: S, newValue: A): S
}


trait FirstInstances {

  implicit def pairPositions[A, B]: First[(A,B), A] with Second[(A, B), B] = new First[(A,B), A] with Second[(A, B), B] {
    def get_1(from: (A, B)): A = from._1
    def get_2(from: (A, B)): B = from._2

    def set_1(from: (A, B), newValue: A): (A, B) = from.copy(_1 = newValue)
    def set_2(from: (A, B), newValue: B): (A, B) = from.copy(_2 = newValue)
  }

  implicit def triplePositions[A, B, C]: First[(A,B,C), A] with Second[(A, B, C), B] = new First[(A,B,C), A] with Second[(A, B, C), B] {
    def get_1(from: (A, B, C)): A = from._1
    def get_2(from: (A, B, C)): B = from._2

    def set_1(from: (A, B, C), newValue: A): (A, B, C) = from.copy(_1 = newValue)
    def set_2(from: (A, B, C), newValue: B): (A, B, C) = from.copy(_2 = newValue)
  }


}

object TuplesInstances extends FirstInstances


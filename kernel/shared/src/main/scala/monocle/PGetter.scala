package monocle

trait PGetter[-S, +T, +A, -B] extends PFold[S, T, A, B] { self =>
  def get(s: S): A
}
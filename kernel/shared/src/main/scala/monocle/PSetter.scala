package monocle

trait PSetter[-S, +T, +A, -B] extends PFold[S, T, A, B] { self =>

  /** modify polymorphically the target of a [[PSetter]] with a function */
  def modify(f: A => B): S => T

  /** set polymorphically the target of a [[PSetter]] with a value */
  def set(b: B): S => T
}
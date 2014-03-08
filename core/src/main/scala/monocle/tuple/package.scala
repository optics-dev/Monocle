package monocle

package object tuple {

  def pairToSecondArg[S, T]: SimpleLens[(S, T), T] = {
    def get(pair: (S, T)): T = pair._2
    def set(pair: (S, T), newValue: T) : (S, T) = pair.copy(_2 = newValue)

    SimpleLens(get, set)
  }

}

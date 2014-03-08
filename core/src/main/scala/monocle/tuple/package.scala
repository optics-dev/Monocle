package monocle

package object tuple {

  def pairToFirstArg[S, T]: SimpleLens[(S, T), S] = {
    def get(pair: (S, T)): S = pair._1
    def set(pair: (S, T), newValue: S) : (S, T) = pair.copy(_1 = newValue)

    SimpleLens(get, set)
  }

  def pairToSecondArg[S, T]: SimpleLens[(S, T), T] = {
    def get(pair: (S, T)): T = pair._2
    def set(pair: (S, T), newValue: T) : (S, T) = pair.copy(_2 = newValue)

    SimpleLens(get, set)
  }

}

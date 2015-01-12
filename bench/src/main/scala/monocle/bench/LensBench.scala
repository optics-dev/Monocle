package monocle.bench

import monocle.bench.BenchModel.Nested0

import scalaz.Maybe


trait LensBench {

  def lensGet0(): Int
  def lensGet3(): Int
  def lensGet6(): Int


  def lensSet0(): Nested0
  def lensSet3(): Nested0
  def lensSet6(): Nested0


  def lensModify0(): Nested0
  def lensModify3(): Nested0
  def lensModify6(): Nested0


  def lensModifyF0(): Maybe[Nested0]
  def lensModifyF3(): Maybe[Nested0]
  def lensModifyF6(): Maybe[Nested0]

  def arrayGet(f: Nested0 => Int): Int =  {
    var result = 0
    var i = 0
    while (i < BenchModel.n0s.length) {
      val value = f(BenchModel.n0s(i))
      if (value > result) result = value
      i = i + 1
    }
    result
  }

  def arraySetModify(f: Nested0 => Nested0): Nested0 =  {
    var result: Nested0 = null
    var maxValue = 0
    var i = 0
    while (i < BenchModel.n0s.length) {
      val value = f(BenchModel.n0s(i))
      if (value.i > maxValue) {
        result = value
        maxValue = value.i
      }
      i = i + 1
    }
    result
  }

  def arrayModifyMaybe(f: Nested0 => Maybe[Nested0]): Maybe[Nested0] =  {
    var result: Nested0 = null
    var maxValue = 0
    var i = 0
    while (i < BenchModel.n0s.length) {
      val value = f(BenchModel.n0s(i))
      value.cata( nested0 =>
        if (nested0.i > maxValue) {
          result = nested0
          maxValue = nested0.i
        }, ()
      )
      i = i + 1
    }
    if(result == null) Maybe.empty else Maybe.just(result)
  }

}

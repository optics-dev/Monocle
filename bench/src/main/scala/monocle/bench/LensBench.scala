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

}

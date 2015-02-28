package monocle.bench

import monocle.bench.BenchModel.Nested0
import monocle.bench.input.Nested0Input

import scalaz.Maybe

trait LensBench {

  def lensGet0(in: Nested0Input): Int
  def lensGet3(in: Nested0Input): Int
  def lensGet6(in: Nested0Input): Int


  def lensSet0(in: Nested0Input): Nested0
  def lensSet3(in: Nested0Input): Nested0
  def lensSet6(in: Nested0Input): Nested0


  def lensModify0(in: Nested0Input): Nested0
  def lensModify3(in: Nested0Input): Nested0
  def lensModify6(in: Nested0Input): Nested0


  def lensModifyF0(in: Nested0Input): Maybe[Nested0]
  def lensModifyF3(in: Nested0Input): Maybe[Nested0]
  def lensModifyF6(in: Nested0Input): Maybe[Nested0]

}

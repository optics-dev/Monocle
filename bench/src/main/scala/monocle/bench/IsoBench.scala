package monocle.bench

import monocle.bench.BenchModel._


trait IsoBench {

  def get0(): Int
  def get3(): Int
  def get6(): Int

  def reverseGet0: IntWrapper0
  def reverseGet3: IntWrapper0
  def reverseGet6: IntWrapper0

  def reverse0: IntWrapper0
  def reverse3: IntWrapper0
  def reverse6: IntWrapper0

  def modify0(): IntWrapper0
  def modify3(): IntWrapper0
  def modify6(): IntWrapper0


}

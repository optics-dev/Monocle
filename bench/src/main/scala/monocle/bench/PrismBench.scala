package monocle.bench

import monocle.bench.BenchModel.ADT
import monocle.bench.input.ADTInput

trait PrismBench {
  def getOption0(in: ADTInput): Option[Int]
  def getOption3(in: ADTInput): Option[Int]
  def getOption6(in: ADTInput): Option[Int]

  def modify0(in: ADTInput): ADT
  def modify3(in: ADTInput): ADT
  def modify6(in: ADTInput): ADT
}

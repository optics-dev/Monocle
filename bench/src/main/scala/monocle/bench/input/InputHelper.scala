package monocle.bench.input

import scala.util.Random

abstract class InputHelper {

  val r = new Random

  def genBool(): Boolean = r.nextBoolean()
  def genInt(): Int = r.nextInt()
  def genLong(): Long = r.nextLong()
  def genStr(): String = r.nextString(r.nextInt(100))

}

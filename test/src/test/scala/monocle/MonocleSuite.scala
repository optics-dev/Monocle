package monocle

import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.scalatest.Discipline

trait MonocleSuite extends FunSuite with Discipline with Matchers with TestInstances
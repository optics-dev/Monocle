package monocle.law

import monocle.internal.IsEq
import org.scalacheck.Prop
import org.scalacheck.Prop._
import org.scalacheck.util.Pretty

import cats.Eq

package object discipline {
  implicit def isEqToProp[A](isEq: IsEq[A])(implicit A: Eq[A]): Prop =
    if(A.eqv(isEq.lhs, isEq.rhs)) proved else falsified :| {
      val exp = Pretty.pretty[A](isEq.rhs, Pretty.Params(0))
      val act = Pretty.pretty[A](isEq.lhs, Pretty.Params(0))
      "Expected "+exp+" but got "+act
    }
}

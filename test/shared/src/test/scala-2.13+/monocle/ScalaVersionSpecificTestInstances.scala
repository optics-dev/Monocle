package monocle

import cats.Eq
import org.scalacheck.Arbitrary

private[monocle] trait ScalaVersionSpecificTestInstances {
  implicit def function1Eq[A, B](implicit A: Arbitrary[A], B: Eq[B]) =
    new Eq[A => B] {
      val samples      = LazyList.continually(A.arbitrary.sample).flatten
      val samplesCount = 50

      override def eqv(f: A => B, g: A => B) =
        samples.take(samplesCount).forall(a => B.eqv(f(a), g(a)))
    }
}

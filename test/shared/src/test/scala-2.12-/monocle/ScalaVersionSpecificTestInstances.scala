package monocle

import cats.Eq
import cats.implicits._
import cats.data.OneAnd
import cats.free.Cofree
import cats.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary._
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Cogen}

private [monocle] trait ScalaVersionSpecificTestInstances {
  implicit def streamCofreeEq[A](implicit A: Eq[A]): Eq[Cofree[Stream, A]] =
    Eq.instance { (a, b) =>  A.eqv(a.head, b.head) && a.tail === b.tail }

  implicit def function1Eq[A, B](implicit A: Arbitrary[A], B: Eq[B]) = new Eq[A => B] {
    val samples = Stream.continually(A.arbitrary.sample).flatten
    val samplesCount = 50

    override def eqv(f: A => B, g: A => B) =
      samples.take(samplesCount).forall { a => B.eqv(f(a), g(a)) }
  }

  implicit def streamCoGen[A: Cogen]: Cogen[Stream[A]] = Cogen[List[A]].contramap[Stream[A]](_.toList)

  implicit def cogenStreamCofree[A](implicit A: Cogen[A]): Cogen[Cofree[Stream, A]] =
    Cogen[Cofree[Stream, A]]((seed: Seed, t: Cofree[Stream, A]) => Cogen[(A, Stream[Cofree[Stream, A]])].perturb(seed, (t.head, t.tail.value)))

  implicit def optionCofreeArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[Cofree[Option, A]] =
    Arbitrary(Arbitrary.arbitrary[OneAnd[List, A]].map( xs =>
      monocle.std.cofree.cofreeToStream.reverseGet(xs.copy(tail = xs.tail.toStream))
    ))
}

package monocle.function

import monocle.MonocleSuite
import monocle.function.Snoc._
import monocle.function.Plated._
import org.scalacheck.Prop._

class PlatedSpec extends MonocleSuite {

  test("children on Stream is consistent with .tail") {
    check { (h: Int, t: Stream[Int]) =>
      val s = h #:: t
      children(s) === List(s.tail)
    }
  }

  test("universe on Stream is consistent with .tails") {
    check { (s: Stream[Int]) =>
      universe(s) === s.tails.toStream
    }
  }

  test("rewrite on Stream is able to change the last node") {
    check { (x: Int, y: Int, z: Int) =>
      rewrite[Stream[Int]] {
        case Stream(n) if n != x =>
          Some(Stream(x))
        case _ => None
      }(Stream(x, y, z)) === Stream(x, y, x)
    }
  }

  test("rewriteOf initOption on Stream is able to change the first node") {
    check { (x: Int, y: Int, z: Int) =>
      rewriteOf(initOption[Stream[Int], Int].asSetter) {
        case Stream(n) if n != z => Some(Stream(z))
        case _ => None
      }(Stream(x, y, z)) === Stream(z, y, z)
    }
  }

  test("transform on Stream can change the last element without a guard") {
    check { (i: Int, x: Int, y: Int, xs: Stream[Int]) =>
      transform[Stream[Int]] {
        case Stream(_) => Stream(i)
        case xs => xs
      }(xs #::: Stream(y, x)) === xs #::: Stream(y, i)
    }
  }

  test("transform initOption on Stream can change the first element without a guard") {
    check { (i: Int, x: Int, y: Int, xs: Stream[Int]) =>
      transformOf(initOption[Stream[Int], Int].asSetter) {
        case Stream(_) => Stream(i)
        case xs => xs
      }(x #:: y #:: xs) === i #:: y #:: xs
    }
  }

  test("transform counting Stream using Option.bind returns count of changes the same as stream size") {
    check { (xs: Stream[Int]) =>
      transformCounting[Stream[Int]](Option(_))(xs) === ((xs.size, xs))
    }
  }


}

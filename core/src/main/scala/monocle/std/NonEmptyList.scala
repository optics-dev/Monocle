package monocle.std

import monocle.function._
import monocle.SimpleOptional

import scala.annotation.tailrec
import scalaz.Id.Id
import scalaz.std.list._
import scalaz.syntax.std.option._
import scalaz.syntax.traverse._
import scalaz.{NonEmptyList, Maybe}

object nonemptylist extends NonEmptyListInstances

trait NonEmptyListInstances {

  def zipWithIndexNel[A](n: NonEmptyList[A]): NonEmptyList[(A, Int)] = {
    n.zip(NonEmptyList(0, Stream.from(1): _*))
  }

  implicit def nelEach[A]: Each[NonEmptyList[A], A] = Each.traverseEach[NonEmptyList, A]

  implicit def nelIndex[A]: Index[NonEmptyList[A], Int, A] = new Index[NonEmptyList[A], Int, A] {

    def index(i: Int) = SimpleOptional[NonEmptyList[A], A](
      il => if (i < 0) Maybe.empty else il.list.drop(i).headOption.toMaybe)(
        (a, il) => zipWithIndexNel(il).traverse[Id, A] {
          case (_, index) if index == i => a
          case (value, index)           => value
        }
      )
  }

  implicit def nelFilterIndex[A]: FilterIndex[NonEmptyList[A], Int, A] =
    FilterIndex.traverseFilterIndex[NonEmptyList, A](n => zipWithIndexNel(n))

  implicit def nelReverse[A]: Reverse[NonEmptyList[A], NonEmptyList[A]] =
    reverseFromReverseFunction[NonEmptyList[A]](_.reverse)

}

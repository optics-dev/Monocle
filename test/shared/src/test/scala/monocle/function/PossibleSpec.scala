package monocle.function

import monocle.Iso
import monocle.MonocleSuite
import monocle.law.discipline.function.PossibleTests
import Possible.optionPossible

class PossibleSpec extends MonocleSuite {

  implicit def optionEitherPossible[A]: Possible[Either[Unit,A], A] = 
  Possible.fromIso(
    Iso[Either[Unit,A], Option[A]] {
      case Right(a) => Some(a)
      case Left(_) => None 
    } {
      case Some(a) => Right(a) 
      case None => Left(()) 
    })

  checkAll("fromIso", PossibleTests[Either[Unit,Int], Int])

}

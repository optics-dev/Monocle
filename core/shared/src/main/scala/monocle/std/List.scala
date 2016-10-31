package monocle.std

import monocle.{Iso, PIso}

object list extends ListOptics

trait ListOptics {
  def pListToVector[A, B]: PIso[List[A], List[B], Vector[A], Vector[B]] =
    PIso[List[A], List[B], Vector[A], Vector[B]](_.toVector)(_.toList)

  def listToVector[A]: Iso[List[A], Vector[A]] =
    pListToVector[A, A]
}

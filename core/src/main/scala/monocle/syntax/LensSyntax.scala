package monocle.syntax

import monocle.Lens

final class LensOps[S, T, A, B](val self: Lens[S, T, A, B]) {
  def oo[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = self compose other
}

trait ToLensOps {
  implicit def toLensOps[S, T, A, B](lens: Lens[S, T, A, B]) = new LensOps[S, T, A, B](lens)
}

trait LensSyntax[S, T, A, B] {
  implicit def toLensOps(lens: Lens[S, T, A, B]) = new LensOps[S, T, A, B](lens)
}

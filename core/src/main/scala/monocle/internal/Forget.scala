package monocle.internal

import scalaz.{\/, Monoid, Profunctor}


final case class Forget[R, A, B](runForget: A => R) extends AnyVal{
  @inline def retag[C]: Forget[R, A, C] = Forget(runForget)
}

object Forget extends ForgetInstances

sealed abstract class ForgetInstances2 {
  implicit def forgetProFunctor[R]: Profunctor[Forget[R, ?, ?]] = new ForgetProFunctor[R]{}
}

sealed abstract class ForgetInstances1 extends ForgetInstances2 {
  implicit def forgetStrong[R]: Strong[Forget[R, ?, ?]] = new ForgetStrong[R]{}
  implicit def forgetProChoice[R: Monoid]: ProChoice[Forget[R, ?, ?]] = new ForgetProChoice[R]{
    def R: Monoid[R] = implicitly
  }
}

sealed abstract class ForgetInstances extends ForgetInstances1 {
  implicit def forgetStep[R: Monoid]: Step[Forget[R, ?, ?]] = new ForgetStep[R]{
    def R: Monoid[R] = implicitly
  }
}



private sealed trait ForgetProFunctor[R] extends Profunctor[Forget[R, ?, ?]]{
  @inline final override def dimap[A, B, C, D](fab: Forget[R, A, B])(f: C => A)(g: B => D): Forget[R, C, D] =
    Forget(fab.runForget compose f)
  @inline final def mapfst[A, B, C](fab: Forget[R, A, B])(f: C => A): Forget[R, C, B] = Forget(fab.runForget compose f)
  @inline final def mapsnd[A, B, C](fab: Forget[R, A, B])(f: B => C): Forget[R, A, C] = fab.retag[C]
}

private sealed trait ForgetStrong[R] extends Strong[Forget[R, ?, ?]] with ForgetProFunctor[R]{
  @inline final override def first[A, B, C](pab: Forget[R, A, B]): Forget[R, (A, C), (B, C)] =
    Forget(ac => pab.runForget(ac._1))
  @inline final override def second[A, B, C](pab: Forget[R, A, B]): Forget[R, (C, A), (C, B)] =
    Forget(ca => pab.runForget(ca._2))
}

private sealed trait ForgetProChoice[R] extends ProChoice[Forget[R, ?, ?]] with ForgetProFunctor[R]{
  def R: Monoid[R]

  @inline final override def left[A, B, C](pab: Forget[R, A, B]): Forget[R, A \/ C, B \/ C] =
    Forget(ac => ac.fold(pab.runForget, _ => R.zero))
  @inline final override def right[A, B, C](pab: Forget[R, A, B]): Forget[R, C \/ A, C \/ B] =
    Forget(ca => ca.fold(_ => R.zero, pab.runForget))
}

private sealed trait ForgetStep[R] extends Step[Forget[R, ?, ?]] with ForgetStrong[R] with ForgetProChoice[R]{
  def R: Monoid[R]

  @inline final override def step[A, B, C, D](pab: Forget[R, A, B]): Forget[R, C \/ (A, D), C \/ (B, D)] =
    Forget(cad => cad.fold(_ => R.zero, ad => pab.runForget(ad._1)))
}
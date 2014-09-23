package monocle

import monocle.internal.{Ap, Walk}

import scalaz.{IList, Kleisli, Applicative, Const, Monoid, Traverse}


/**
 * A Traversal is generalisation of a Lens in a way that it defines a multi foci between
 * S and 0 to many A.
 */
abstract class Traversal[S, T, A, B] { self =>

  def _traversal[P[_, _]: Walk]: Optic[P, S, T, A, B]

  final def modifyK[F[_]: Applicative](f: Kleisli[F, A, B]): Kleisli[F, S, T] =
    _traversal[Kleisli[F, ?, ?]].apply(f)

  final def getAll(s: S): IList[A] = modifyK[Const[IList[A], ?]](
    Kleisli[Const[IList[A], ?], A, B](a => Const(IList(a)))
  ).run(s).getConst

  final def modify(f: A => B): S => T = _traversal[Function1].apply(f)
  final def set(b: B): S => T = modify(_ => b)

  // Compose
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] = asFold composeFold other
  final def composeSetter[C, D](other: Setter[A, B, C, D]): Setter[S, T, C, D] = asSetter composeSetter other
  final def composeTraversal[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = new Traversal[S, T, C, D] {
    def _traversal[P[_, _] : Walk]: Optic[P, S, T, C, D] = self._traversal[P] compose other._traversal[P]
  }
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composePrism[C, D](other: Prism[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composeLens[C, D](other: Lens[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)
  final def composeIso[C, D](other: Iso[A, B, C, D]): Traversal[S, T, C, D] = composeTraversal(other.asTraversal)

  // Optic transformation
  final def asSetter: Setter[S, T, A, B] = Setter[S, T, A, B](modify)
  final def asFold: Fold[S, A] = new Fold[S, A]{
    def foldMap[M: Monoid](f: A => M)(s: S): M = modifyK[Const[M, ?]](
      Kleisli[Const[M, ?], A, B](a => Const(f(a)))
    ).run(s).getConst
  }

}

object Traversal {

  def apply[T[_]: Traverse, A, B]: Traversal[T[A], T[B], A, B] = new Traversal[T[A], T[B], A, B] {
    def _traversal[P[_, _]: Walk]: Optic[P, T[A], T[B], A, B] = ???
  }

  def apply2[S, T, A, B](get1: S => A, get2: S => A)(_set: (B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[P[_, _]: Walk]: Optic[P, S, T, A, B] = pab =>
      Applicative[Ap[P, S, ?]].apply3(Ap(Walk[P].mapfst(pab)(get1)), Ap(Walk[P].mapfst(pab)(get2)), Ap(Walk[P].pureP[S]))(_set).pab
  }

  def apply3[S, T, A, B](get1: S => A, get2: S => A, get3: S => A)(_set: (B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[P[_, _] : Walk]: Optic[P, S, T, A, B] = pab =>
      Applicative[Ap[P, S, ?]].apply4(Ap(Walk[P].mapfst(pab)(get1)), Ap(Walk[P].mapfst(pab)(get2)), Ap(Walk[P].mapfst(pab)(get3)), Ap(Walk[P].pureP[S]))(_set).pab
  }

  def apply4[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A)(_set: (B, B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[P[_, _] : Walk]: Optic[P, S, T, A, B] = pab =>
      Applicative[Ap[P, S, ?]].apply5(Ap(Walk[P].mapfst(pab)(get1)), Ap(Walk[P].mapfst(pab)(get2)), Ap(Walk[P].mapfst(pab)(get3)), Ap(Walk[P].mapfst(pab)(get4)), Ap(Walk[P].pureP[S]))(_set).pab
  }

  def apply5[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A)(_set: (B, B, B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[P[_, _] : Walk]: Optic[P, S, T, A, B] = pab =>
      Applicative[Ap[P, S, ?]].apply6(Ap(Walk[P].mapfst(pab)(get1)), Ap(Walk[P].mapfst(pab)(get2)), Ap(Walk[P].mapfst(pab)(get3)), Ap(Walk[P].mapfst(pab)(get4)), Ap(Walk[P].mapfst(pab)(get5)), Ap(Walk[P].pureP[S]))(_set).pab
  }

  def apply6[S, T, A, B](get1: S => A, get2: S => A, get3: S => A, get4: S => A, get5: S => A, get6: S => A)(_set: (B, B, B, B, B, B, S) => T): Traversal[S, T, A, B] = new Traversal[S, T, A, B] {
    def _traversal[P[_, _] : Walk]: Optic[P, S, T, A, B] = pab =>
      Applicative[Ap[P, S, ?]].apply7(Ap(Walk[P].mapfst(pab)(get1)), Ap(Walk[P].mapfst(pab)(get2)), Ap(Walk[P].mapfst(pab)(get3)), Ap(Walk[P].mapfst(pab)(get4)), Ap(Walk[P].mapfst(pab)(get5)), Ap(Walk[P].mapfst(pab)(get6)), Ap(Walk[P].pureP[S]))(_set).pab
  }

}

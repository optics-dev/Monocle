package lens.impl

import lens.Lens
import lens.util.Constant
import scala.language.higherKinds
import scalaz.Functor


trait HaskLens[A, B] extends Lens[A,B] {
  protected def lensFunction[F[_] : Functor](lift: B => F[B], a: A): F[A]

  def get(a: A): B = {
    val b2Fb: B => Constant[B, B] = { b: B => Constant(b)}
    lensFunction[({type l[a] = Constant[B,a]})#l] (b2Fb, a).value
  }

  def lift[F[_] : Functor](from: A, f: B => F[B]): F[A] = lensFunction(f, from)

  // overload
  def >-[C](other: HaskLens[B, C]): Lens[A, C] = HaskLens.compose(this, other)
}

object HaskLens {

  def compose[A, B, C](a2b: HaskLens[A, B], b2C: HaskLens[B, C]): HaskLens[A,C] = new HaskLens[A, C] {
    // (b -> f b) -> a -> f a  and (c -> f c) -> b -> f b
    // (c -> f c) -> a -> f a
    protected def lensFunction[F[_] : Functor](lift: C => F[C], a: A): F[A] =
      a2b.lensFunction({b: B => b2C.lensFunction(lift, b)}, a)
  }


}





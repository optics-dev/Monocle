package lens.impl

import lens.Lens
import lens.util.Constant
import scala.language.higherKinds
import scalaz.Functor


trait HLens[A, B] extends Lens[A,B] {
  protected def lensFunction[F[_] : Functor](lift: B => F[B], a: A): F[A]

  def get(from: A): B = {
    val b2Fb: B => Constant[B, B] = { b: B => Constant(b)}
    lensFunction[({type l[a] = Constant[B,a]})#l] (b2Fb, from).value
  }

  def lift[F[_] : Functor](from: A, f: B => F[B]): F[A] = lensFunction(f, from)

  // overload
  def >-[C](other: HLens[B, C]): HLens[A, C] = HLens.compose(this, other)
}

object HLens {

  def compose[A, B, C](a2b: HLens[A, B], b2C: HLens[B, C]): HLens[A,C] = new HLens[A, C] {
    // (b -> f b) -> a -> f a  and (c -> f c) -> b -> f b
    // (c -> f c) -> a -> f a
    protected def lensFunction[F[_] : Functor](lift: C => F[C], a: A): F[A] =
      a2b.lensFunction({b: B => b2C.lensFunction(lift, b)}, a)
  }


}





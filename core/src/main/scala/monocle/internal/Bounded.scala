package monocle.internal


private[monocle] trait Bounded[T] {
  def MinValue: T
  def MaxValue: T
}

private[monocle] object Bounded extends BoundedInstances {
  def apply[T](implicit ev: Bounded[T]): Bounded[T] = ev
}

private[monocle] trait BoundedInstances {

  implicit val booleanBounded = new Bounded[Boolean]{
    val MaxValue: Boolean = true
    val MinValue: Boolean = false
  }

  implicit val byteBounded= new Bounded[Byte] {
    val MaxValue: Byte = Byte.MaxValue
    val MinValue: Byte = Byte.MinValue
  }

  implicit val charBounded = new Bounded[Char] {
    val MaxValue: Char = Char.MaxValue
    val MinValue: Char = Char.MinValue
  }

  implicit val intBounded = new Bounded[Int] {
    val MaxValue: Int = Int.MaxValue
    val MinValue: Int = Int.MinValue
  }


}
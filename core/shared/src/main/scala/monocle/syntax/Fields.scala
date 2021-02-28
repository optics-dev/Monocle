package monocle.syntax

import monocle.Lens
import monocle.function._

object fields extends FieldsSyntax

trait FieldsSyntax {
  @deprecated("use Focus[$TupleType](_._1)", since = "3.0.0-M2")
  def _1[S, A](implicit ev: Field1[S, A]): Lens[S, A] = ev.first
  @deprecated("use Focus[$TupleType](_._2)", since = "3.0.0-M2")
  def _2[S, A](implicit ev: Field2[S, A]): Lens[S, A] = ev.second
  @deprecated("use Focus[$TupleType](_._3)", since = "3.0.0-M2")
  def _3[S, A](implicit ev: Field3[S, A]): Lens[S, A] = ev.third
  @deprecated("use Focus[$TupleType](_._4)", since = "3.0.0-M2")
  def _4[S, A](implicit ev: Field4[S, A]): Lens[S, A] = ev.fourth
  @deprecated("use Focus[$TupleType](_._5)", since = "3.0.0-M2")
  def _5[S, A](implicit ev: Field5[S, A]): Lens[S, A] = ev.fifth
  @deprecated("use Focus[$TupleType](_._6)", since = "3.0.0-M2")
  def _6[S, A](implicit ev: Field6[S, A]): Lens[S, A] = ev.sixth
}

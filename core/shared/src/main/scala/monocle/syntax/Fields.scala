package monocle.syntax

import monocle.Lens
import monocle.function._

object fields extends FieldsSyntax

trait FieldsSyntax {
  @deprecated("use monocle.function.At.at(1)", since = "3.0.0-M1")
  def _1[S, A](implicit ev: Field1[S, A]): Lens[S, A] = ev.first
  @deprecated("use monocle.function.At.at(2)", since = "3.0.0-M1")
  def _2[S, A](implicit ev: Field2[S, A]): Lens[S, A] = ev.second
  @deprecated("use monocle.function.At.at(3)", since = "3.0.0-M1")
  def _3[S, A](implicit ev: Field3[S, A]): Lens[S, A] = ev.third
  @deprecated("use monocle.function.At.at(4)", since = "3.0.0-M1")
  def _4[S, A](implicit ev: Field4[S, A]): Lens[S, A] = ev.fourth
  @deprecated("use monocle.function.At.at(5)", since = "3.0.0-M1")
  def _5[S, A](implicit ev: Field5[S, A]): Lens[S, A] = ev.fifth
  @deprecated("use monocle.function.At.at(6)", since = "3.0.0-M1")
  def _6[S, A](implicit ev: Field6[S, A]): Lens[S, A] = ev.sixth
}

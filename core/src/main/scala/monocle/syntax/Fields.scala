package monocle.syntax

import monocle.SimpleLens
import monocle.function._

object fields extends FieldsSyntax

trait FieldsSyntax {

  def _1[S, A](implicit ev: Field1[S, A]): SimpleLens[S, A] = ev.first
  def _2[S, A](implicit ev: Field2[S, A]): SimpleLens[S, A] = ev.second
  def _3[S, A](implicit ev: Field3[S, A]): SimpleLens[S, A] = ev.third
  def _4[S, A](implicit ev: Field4[S, A]): SimpleLens[S, A] = ev.fourth
  def _5[S, A](implicit ev: Field5[S, A]): SimpleLens[S, A] = ev.fifth
  def _6[S, A](implicit ev: Field6[S, A]): SimpleLens[S, A] = ev.sixth

}

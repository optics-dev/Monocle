package monocle

// Experiment in generic, type-inferred compose
trait ComposeImplicits {
  trait Compose[F[_, _, _, _], G[_, _, _, _]] {
    type Result[_, _, _, _]
  }

  implicit val POptionalPOptional = new Compose[POptional, POptional] {
    type Result[S, T, A, B] = POptional[S, T, A, B]
  }
  implicit val PLensPLens = new Compose[PLens, PLens] {
    type Result[S, T, A, B] = PLens[S, T, A, B]
  }

  def testCompose[S, T, A, B](
    first: POptional[S, T, A, B],
    second: POptional[S, T, A, B]
  )(implicit ev: Compose[POptional, POptional]): ev.Result[S, T, A, B] = 
    ???

  def testCompose[S, T, A, B](
    first: PLens[S, T, A, B],
    second: PLens[S, T, A, B]
  )(implicit ev: Compose[PLens, PLens]): ev.Result[S, T, A, B] = 
    ???

  implicit class OpticSyntax[S, T, A, B](optic: POptional[S, T, A, B]) {
    def compose(that: POptional[S, T, A, B]
    )(implicit ev: Compose[POptional, POptional]): ev.Result[S, T, A, B] = 
      ???

    def compose(second: PLens[S, T, A, B]
    )(implicit ev: Compose[PLens, PLens]): ev.Result[S, T, A, B] = 
      ???
  }

  type T = PLens[Int, String, Boolean, Double]
  (??? : T).compose(??? : T) : POptional[Int,String,Boolean,Double]
}
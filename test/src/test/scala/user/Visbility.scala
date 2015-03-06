package user

class Visbility {

  /* Macro should work outside of monocle package */
  import monocle.macros.GenLens
  case class Foo(n: Int)
  object Foo {
    val lens = GenLens[Foo](_.n)
  }
}

package monocle

import monocle.macros.GenLens

object Focus {

  /**
    * Focus in Scala 2 is limited to generating Lenses for case classes.
    * In Scala 3, the macro is much more powerful and resides in the core module.
    */
  def apply[A] = new GenLens[A]

}


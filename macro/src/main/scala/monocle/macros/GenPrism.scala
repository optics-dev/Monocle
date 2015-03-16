package monocle.macros

import monocle.Prism
import monocle.macros.internal.MacrosCompatibility




object GenPrism {
  /** generate a [[Prism]] between `S` and a subtype `A` of `S` */
  def apply[S, A <: S]: Prism[S, A] = macro GenPrismImpl.genPrism_impl[S, A]
}

private object GenPrismImpl extends MacrosCompatibility {
  def genPrism_impl[S: c.WeakTypeTag, A: c.WeakTypeTag](c: Context): c.Expr[Prism[S, A]] = {
    import c.universe._

    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val sTpeSym = companionTpe(c)(sTpe)
    c.Expr[Prism[S, A]](q"""
      import monocle.Prism
      import scalaz.{\/, \/-, -\/, Maybe}

      new Prism[$sTpe, $aTpe]{
        def getOrModify(s: $sTpe): $sTpe \/ $aTpe =
          if(s.isInstanceOf[$aTpe]) \/-(s.asInstanceOf[$aTpe])
          else -\/(s)

        def reverseGet(a: $aTpe): $sTpe =
          a.asInstanceOf[$sTpe]

        def getMaybe(s: $sTpe): Maybe[$aTpe] =
          if(s.isInstanceOf[$aTpe]) Maybe.just(s.asInstanceOf[$aTpe])
          else Maybe.empty
      }
    """)
  }
}
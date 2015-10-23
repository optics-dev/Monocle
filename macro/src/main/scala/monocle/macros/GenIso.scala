package monocle.macros

import monocle.Iso
import monocle.macros.internal.MacrosCompatibility

import scala.reflect.internal.SymbolTable

object GenIso {
  /** generate an [[Iso]] between a case class `S` and its unique field of type `A` */
  def apply[S, A]: Iso[S, A] = macro GenIsoImpl.genIso_impl[S, A]

  /** generate an [[Iso]] between an object `S` and Unit */
  def unit[S]: Iso[S, Unit] = macro GenIsoImpl.genIso_unit_impl[S]
}

private object GenIsoImpl extends MacrosCompatibility {
  private def caseAccessorsOf[S: c.WeakTypeTag](c: Context): List[c.universe.MethodSymbol] = {
    import c.universe._
    getDeclarations(c)(weakTypeOf[S]).collect { case m: MethodSymbol if m.isCaseAccessor => m }.toList
  }

  def genIso_impl[S: c.WeakTypeTag, A: c.WeakTypeTag](c: Context): c.Expr[Iso[S, A]] = {
    import c.universe._

    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val fieldMethod = caseAccessorsOf[S](c) match {
      case m :: Nil => m
      case Nil      => c.abort(c.enclosingPosition, s"Cannot find a case class accessor for $sTpe, $sTpe needs to be a case class with a single accessor")
      case _        => c.abort(c.enclosingPosition, s"Found several case class accessor for $sTpe, $sTpe needs to be a case class with a single accessor")
    }

    val sTpeSym = companionTpe(c)(sTpe)

    c.Expr[Iso[S, A]](q"""
      import monocle.Iso
      import scalaz.Functor

      new Iso[$sTpe, $aTpe]{ self =>
        def get(s: $sTpe): $aTpe =
          s.$fieldMethod

        def reverseGet(a: $aTpe): $sTpe =
         $sTpeSym(a)

        def reverse: Iso[$aTpe, $sTpe] =
          new Iso[$aTpe, $sTpe]{
            def get(a: $aTpe): $sTpe =
              $sTpeSym(a)

            def reverseGet(s: $sTpe): $aTpe =
              s.$fieldMethod

            def reverse: Iso[$sTpe, $aTpe] =
              self
          }
      }
    """)
  }

  def genIso_unit_impl[S: c.WeakTypeTag](c: Context): c.Expr[Iso[S, Unit]] = {
    import c.universe._

    val sTpe = weakTypeOf[S]

    if (sTpe.typeSymbol.isModuleClass) {
      val table = c.universe.asInstanceOf[SymbolTable]
      val obj = makeAttributedQualifier(c)(table.gen, sTpe)
      c.Expr[Iso[S, Unit]](q"""
        monocle.Iso[${sTpe}, Unit](Function.const(()))(Function.const(${obj}))
      """)
    } else {
      caseAccessorsOf[S](c) match {
        case Nil =>
          val sTpeSym = companionTpe(c)(sTpe)
          c.Expr[Iso[S, Unit]](q"""
            monocle.Iso[${sTpe}, Unit](Function.const(()))(Function.const(${sTpeSym}()))
          """)
        case _   => c.abort(c.enclosingPosition, s"$sTpe needs to be a case class with no accessor or an object.")
      }
    }
  }
}

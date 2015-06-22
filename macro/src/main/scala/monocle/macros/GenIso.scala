package monocle.macros

import monocle.Iso
import monocle.macros.internal.MacrosCompatibility

import scala.reflect.internal.SymbolTable

object GenIso {
  /** generate an [[Iso]] between a case class `S` and its unique field of type `A` */
  def apply[S, A]: Iso[S, A] = macro GenIsoImpl.genIso_impl[S, A]

  /** generate an [[Iso]] between an object `S` and Unit */
  def obj[S]: Iso[S, Unit] = macro GenIsoImpl.genIso_obj_impl[S]
}

private object GenIsoImpl extends MacrosCompatibility {
  def genIso_impl[S: c.WeakTypeTag, A: c.WeakTypeTag](c: Context): c.Expr[Iso[S, A]] = {
    import c.universe._

    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val fieldMethod = getDeclarations(c)(sTpe).collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }.toList match {
      case m :: Nil if m.returnType == aTpe => m
      case m :: Nil => c.abort(c.enclosingPosition, s"Found a case class accessor of type ${m.returnType} instead of $aTpe")
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

  def genIso_obj_impl[S: c.WeakTypeTag](c: Context): c.Expr[Iso[S, Unit]] = {
    import c.universe._

    val table = c.universe.asInstanceOf[SymbolTable]
    val sTpe = weakTypeOf[S]

    if (!sTpe.typeSymbol.isModuleClass)
       c.abort(c.enclosingPosition, s"${sTpe} needs to be an object to generate an Iso[${sTpe}, Unit]")

    val obj = table.gen.mkAttributedQualifier(sTpe.asInstanceOf[table.Type]).asInstanceOf[Tree]

    c.Expr[Iso[S, Unit]](q"""
      monocle.Iso[${sTpe}, Unit](Function.const(()))(Function.const(${obj}))
    """)
  }
}

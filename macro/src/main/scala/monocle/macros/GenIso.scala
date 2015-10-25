package monocle.macros

import monocle.Iso

import scala.reflect.internal.SymbolTable
import scala.reflect.macros.blackbox

object GenIso {
  /** generate an [[Iso]] between a case class `S` and its unique field of type `A` */
  def apply[S, A]: Iso[S, A] = macro GenIsoImpl.genIso_impl[S, A]

  /** generate an [[Iso]] between an object `S` and Unit */
  def unit[S]: Iso[S, Unit] = macro GenIsoImpl.genIso_unit_impl[S]
}

@macrocompat.bundle
class GenIsoImpl(val c: blackbox.Context) {
  private def caseAccessorsOf[S: c.WeakTypeTag]: List[c.universe.MethodSymbol] = {
    import c.universe._
    weakTypeOf[S].decls.collect { case m: MethodSymbol if m.isCaseAccessor => m }.toList
  }

  def genIso_impl[S: c.WeakTypeTag, A: c.WeakTypeTag]: c.Expr[Iso[S, A]] = {
    import c.universe._

    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val fieldMethod = caseAccessorsOf[S] match {
      case m :: Nil => m
      case Nil      => c.abort(c.enclosingPosition, s"Cannot find a case class accessor for $sTpe, $sTpe needs to be a case class with a single accessor")
      case _        => c.abort(c.enclosingPosition, s"Found several case class accessor for $sTpe, $sTpe needs to be a case class with a single accessor")
    }

    val sTpeSym = sTpe.typeSymbol.companion

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

  def genIso_unit_impl[S: c.WeakTypeTag]: c.Expr[Iso[S, Unit]] = {
    import c.universe._

    val sTpe = weakTypeOf[S]

    if (sTpe.typeSymbol.isModuleClass) {
      val table = c.universe.asInstanceOf[SymbolTable]
      val tree = table.gen
      val obj = tree.mkAttributedQualifier(sTpe.asInstanceOf[tree.global.Type]).asInstanceOf[c.universe.Tree]
      c.Expr[Iso[S, Unit]](q"""
        monocle.Iso[${sTpe}, Unit](Function.const(()))(Function.const(${obj}))
      """)
    } else {
      caseAccessorsOf[S] match {
        case Nil =>
          val sTpeSym = sTpe.typeSymbol.companion
          c.Expr[Iso[S, Unit]](q"""
            monocle.Iso[${sTpe}, Unit](Function.const(()))(Function.const(${sTpeSym}()))
          """)
        case _   => c.abort(c.enclosingPosition, s"$sTpe needs to be a case class with no accessor or an object.")
      }
    }
  }
}

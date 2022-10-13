package monocle.macros

import monocle.Iso

import scala.reflect.internal.SymbolTable
import scala.reflect.macros.{blackbox, whitebox}

object GenIso {

  /** Generate an [[Iso]] between a case class `S` and its unique field of type `A`. */
  def apply[S, A]: Iso[S, A] = macro GenIsoImpl.genIso_impl[S, A]

  /** Generate an [[Iso]] between an object `S` and `Unit`. */
  def unit[S]: Iso[S, Unit] = macro GenIsoImpl.genIso_unit_impl[S]

  /** Generate an [[Iso]] between a case class `S` and its fields.
    *
    * Case classes with 0 fields will correspond with `Unit`, 1 with the field type, 2 or more with a tuple of all field
    * types in the same order as the fields themselves.
    *
    * Case classes with multiple parameter-lists (example: `case class X(…)(…)`) are rejected.
    */
  def fields[S]: Iso[S, _] = macro GenIsoImplW.genIso_fields_impl[S]
}

sealed abstract class GenIsoImplBase {
  val c: blackbox.Context
  import c.universe._

  final protected def fail(msg: String): Nothing =
    c.abort(c.enclosingPosition, msg)

  final protected def caseAccessorsOf[S: c.WeakTypeTag]: List[MethodSymbol] =
    weakTypeOf[S].decls.collect { case m: MethodSymbol if m.isCaseAccessor => m }.toList

  final protected def genIso_unit_tree[S: c.WeakTypeTag]: c.Tree = {
    val sTpe = weakTypeOf[S].dealias

    if (sTpe.typeSymbol.isModuleClass) {
      val obj: Tree =
        if (sTpe.termSymbol.isTerm)
          q"${sTpe.termSymbol.asTerm}"
        else {
          val table = c.universe.asInstanceOf[SymbolTable]
          val tree  = table.gen
          tree.mkAttributedQualifier(sTpe.asInstanceOf[tree.global.Type]).asInstanceOf[Tree]
        }
      q"""
        monocle.Iso[$sTpe, Unit](Function.const(()))(Function.const($obj))
      """
    } else
      caseAccessorsOf[S] match {
        case Nil =>
          val sTpeSym = sTpe.typeSymbol.companion
          q"""
            monocle.Iso[$sTpe, Unit](Function.const(()))(Function.const($sTpeSym()))
          """
        case _ => fail(s"$sTpe needs to be a case class with no accessor or an object.")
      }
  }
}

class GenIsoImpl(override val c: blackbox.Context) extends GenIsoImplBase {
  import c.universe._

  def genIso_impl[S: c.WeakTypeTag, A: c.WeakTypeTag]: c.Expr[Iso[S, A]] = {
    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val fieldMethod = caseAccessorsOf[S] match {
      case m :: Nil => m
      case Nil =>
        fail(s"Cannot find a case class accessor for $sTpe, $sTpe needs to be a case class with a single accessor.")
      case _ =>
        fail(s"Found several case class accessor for $sTpe, $sTpe needs to be a case class with a single accessor.")
    }

    val sTpeSym = sTpe.typeSymbol.companion

    c.Expr[Iso[S, A]](q"""
      import monocle.Iso

      new Iso[$sTpe, $aTpe]{ self =>
        override def get(s: $sTpe): $aTpe =
          s.$fieldMethod

        override def reverseGet(a: $aTpe): $sTpe =
         $sTpeSym(a)

        override def reverse: Iso[$aTpe, $sTpe] =
          new Iso[$aTpe, $sTpe]{
            override def get(a: $aTpe): $sTpe =
              $sTpeSym(a)

            override def reverseGet(s: $sTpe): $aTpe =
              s.$fieldMethod

            override def reverse: Iso[$sTpe, $aTpe] =
              self
          }
      }
    """)
  }

  def genIso_unit_impl[S: c.WeakTypeTag]: c.Expr[Iso[S, Unit]] =
    c.Expr[Iso[S, Unit]](genIso_unit_tree[S])
}

class GenIsoImplW(override val c: whitebox.Context) extends GenIsoImplBase {
  import c.universe._

  final protected def nameAndType(T: Type, s: Symbol): (TermName, Type) = {
    def paramType(name: TermName): Type =
      T.decl(name).typeSignatureIn(T) match {
        case NullaryMethodType(t) => t
        case t                    => t
      }

    val a = s.asTerm.name match {
      case n: TermName => n
      case n           => fail("Expected a TermName, got " + n)
    }
    val A = paramType(a)
    (a, A)
  }

  def genIso_fields_impl[S: c.WeakTypeTag]: Tree = {
    val sTpe = weakTypeOf[S]

    val sTpeSym = sTpe.typeSymbol.asClass
    if (!sTpeSym.isCaseClass)
      fail(s"$sTpe is not a case class.")

    val paramLists = sTpe.decls
      .collectFirst { case m: MethodSymbol if m.isPrimaryConstructor => m }
      .getOrElse(fail(s"Unable to discern primary constructor for $sTpe."))
      .paramLists

    paramLists match {
      case Nil | Nil :: Nil =>
        genIso_unit_tree[S]

      case (param :: Nil) :: Nil =>
        val (pName, pType) = nameAndType(sTpe, param)
        q"""
          monocle.Iso[$sTpe, $pType](_.$pName)(${sTpeSym.companion}(_))
        """

      case params :: Nil =>
        var readField = List.empty[Tree]
        var readTuple = List.empty[Tree]
        var types     = List.empty[Type]
        for ((param, i) <- params.zipWithIndex.reverse) {
          val (pName, pType) = nameAndType(sTpe, param)
          readField ::= q"s.$pName"
          readTuple ::= q"t.${TermName("_" + (i + 1))}"
          types ::= pType
        }
        q"""
          monocle.Iso[$sTpe, (..$types)](s => (..$readField))(t => ${sTpeSym.companion}(..$readTuple))
        """

      case _ :: _ :: _ =>
        fail(s"Found several parameter-lists for $sTpe, $sTpe needs to be a case class with a single parameter-list.")
    }
  }
}

package monocle.syntax.macros

import monocle.syntax.AppliedLens

import scala.reflect.macros.blackbox


//class GenAppliedLensOps[A, B](private val value: AppliedLens[A, B]) extends AnyVal {
//  def field[C](field: B => C): AppliedLens[A, C] = macro GenAppliedLensOpsImpl.lens_impl[B, C]
//}

class GenAppliedLensOpsImpl(val c: blackbox.Context){
  def lens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag](field: c.Expr[B => C]): c.Expr[AppliedLens[A, C]] = {
    import c.universe._

    val subj = c.prefix.tree
//    val subj = c.prefix.tree match {
//      case Apply(TypeApply(_, _), List(x)) => x
//      case t =>
//        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
//    }

//    val foo: monocle.syntax.AppliedLens[${c.weakTypeOf[A]}, ${c.weakTypeOf[B]}] = $subj
//    val fieldLens: monocle.Lens[${c.weakTypeOf[B]}, ${c.weakTypeOf[C]}] = monocle.macros.GenLens[${c.weakTypeOf[B]}](${field})

    val code: c.universe.Tree = q"""
      val foo: monocle.syntax.AppliedLens[${c.weakTypeOf[A]}, ${c.weakTypeOf[B]}] = $subj

      val fieldLens: monocle.Lens[${c.weakTypeOf[B]}, ${c.weakTypeOf[C]}] = monocle.macros.GenLens[${c.weakTypeOf[B]}](${field})

      foo.compose(fieldLens)
    """

//    println(showCode(code))
    println(showCode(subj))
    println(show(subj))
    println(showRaw(subj))

    c.Expr[AppliedLens[A, C]](code)
  }
}
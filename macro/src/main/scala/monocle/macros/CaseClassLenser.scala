package monocle.macros

import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import monocle.Lens
import scala.language.experimental.macros

/** Contains macros to create lenses for whole case classes (without annotations).
 *
 *  Usage example:
 *  {{{
 *  case class Point(x: Int, y: Int)
 *  val lenses = CaseClassLenser.getLenses[Point]
 *
 *  val update = lenses.x.modify(_ + 100) compose lenses.y.set(7)
 *  update(Point(1, 2)) ==== Point(101, 7)
 *  }}}
 *
 *  or the same with wildcard import:
 *  {{{
 *  import lenses._
 *
 *  val update = x.modify(_ + 100) compose y.set(7)
 *  update(Point(1, 2)) ==== Point(101, 7)
 *  }}}
 *
 *  Lens creation can take an optional prefix, like so:
 *  {{{
 *  val lenses = CaseClassLenser.getLenses[Point]("_")
 *
 *  import lenses._
 *
 *  val update = _x.modify(_ + 100) compose _y.set(7)
 *  update(Point(1, 2)) ==== Point(101, 7)
 *  }}}
 *
 *  These macros generate structural types that contain all the required lenses
 *  as fields. To avoid using reflection to access the fields of the result, the
 *  macro uses the trick described here:
 *
 *  [[https://gist.github.com/xeno-by/5967900]]
 *  [[http://meta.plasm.us/posts/2013/07/12/vampire-methods-for-structural-types/]]
 *
 *  The upside is that the user is not forced to enable the reflective calls
 *  language flag. But on the other hand, as a result all generated lenses
 *  are inlined in the places where they are referenced. This may result
 *  in some compilation overhead if the fields are referenced many times.
 *  As for each reference the compiler generates a number of anonymous classes
 *  that correspond to the various functions used by the lens.
 */
object CaseClassLenser {
  /** A macro that creates an object with lenses for all the field in the case class
   *  type.
   *
   *  The resulting object is a structural type with each field corresponding
   *  to a field in the case class, whose value is a lens for that field.
   *
   *  See use usage examples in [[CaseClassLenser]]'s header.
   *
   *  @tparam A Must be a type corresponding to a case class.
   */
  def getLenses[A]: AnyRef = macro internal.CaseClassLenserImpl.Helper.noPrefix[A]

  /** The same as [[getLenses]] but takes an extra argument to
   *  be a prefix for all the lens fields.
   *
   *  See use usage examples in [[CaseClassLenser]]'s header.
   *
   *  @param prefix The prefix for the lens fields, must be a compile-time constant.
   *  @tparam A Must be a type corresponding to a case class.
   */
  def getLenses[A](prefix: String): AnyRef = macro internal.CaseClassLenserImpl.Helper.lenses[A]
}

package internal {
  // making this `private[macros]` fails to compile as we are inlining the macro call in the user's code
  object CaseClassLenserImpl {
    class Helper(val c: Context) {
      import c.universe._

      /** The macro with the default empty prefix. */
      def noPrefix[A: c.WeakTypeTag] = lenses[A](q""" "" """)

      /** The macro implementation. */
      def lenses[A: c.WeakTypeTag](prefix: c.Tree) = {
        import c.universe._

        val tpe = weakTypeOf[A]

        val isCaseClass = {
          val sym = tpe.typeSymbol
          if (sym.isClass) sym.asClass.isCaseClass else false
        }

        val prefixStr = prefix match {
          case Literal(Constant(s: String)) => s
          case _ => c.abort(c.enclosingPosition, s"Lenses prefix must be a compile-time constant, given [$prefix]")
        }

        def lenses(fields: Iterable[(Type, String)]): Iterable[Tree] =
          fields map {
            case (paramType, name) =>
              val lensName = TermName(prefixStr + name)

              /* 
               * By using the 'Body' annotation here we avoid reflective calls on structural types, 
               * this way all calls to the generated lenses are inlined.
               * In principle, we could've generated the code for the macro here
               * (where we have all the relevant information already available),
               * but as this macro is going to be inlined at all call sites
               * there may be problems with the 'ownership' of the generated trees,
               * e.g. who is the enclosing class of the generated anonymous functions.
               * To avoid all these potential complications, we defer creating
               * the actual code to the `makeBodyImpl` macro, which will appear
               * directly at the call site.
               */
              q"""
                @monocle.macros.internal.CaseClassLenserImpl.Body($name) 
                def $lensName: monocle.Lens[$tpe, $paramType] = 
                    macro monocle.macros.internal.CaseClassLenserImpl.makeBodyImpl
              """
          }

        val res =
          if (isCaseClass) {
            val fields = tpe.decls.collect {
              case m: MethodSymbol if m.isCaseAccessor => (m.returnType, m.name.decodedName.toString)
            }
            q"""
              new {
                import scala.language.experimental.macros
                ..${lenses(fields)}
              }
            """
          } else c.abort(c.enclosingPosition, s"Invalid lensing target [$tpe]: must be a case class")

        res
      }
    }

    /** A container for code to be inserted in a macro.
     *  This is an implementation of the trick described in [[https://gist.github.com/xeno-by/5967900]]
     *  to avoid reflection on a structural types
     */
    class Body(tree: Any) extends StaticAnnotation

    /** A macro that uses the [[Body]] annotation to create a macro that invokes lens creation. */
    def makeBodyImpl(c: Context) = {
      import c.universe._
      val field = c.macroApplication.symbol

      val bodyAnn = field.annotations.filter(_.tree.tpe <:< c.universe.weakTypeOf[Body]).head

      val t: Tree = bodyAnn.tree.children.tail.head
      val Literal(Constant(fieldName: String)) = t

      // getting the expected signature of the lens, as type inference gives the wrong result here
      val TypeRef(_, _, List(s, a)) = field.typeSignature.resultType

      q"monocle.macros.internal.Macro.mkLens[$s, $a]($fieldName)"
    }
  }
}
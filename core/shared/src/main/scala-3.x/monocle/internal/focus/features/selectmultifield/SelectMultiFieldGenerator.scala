package monocle.internal.focus.features.selectmultifield

import monocle.internal.focus.FocusBase
import monocle.Lens
import scala.quoted.Quotes

private[focus] trait SelectMultiFieldGenerator {
  this: FocusBase => 

  import macroContext.reflect._


// Match(
//   Ident(_$2),
//   List(
//     CaseDef(
//       Bind(x,Typed(Ident(_),Ident(Hammer))),
//       EmptyTree,
//       Block(
//         List(),
//         Typed(
//           Select(Ident(x), weight), 
//           TypeTree[TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),class Int)]
//         )
//       )
//     ), 
//     CaseDef(
//       Bind(x,Typed(Ident(_),Ident(Spanner))),
//       EmptyTree,
//       Block(
//         List(),
//         Typed(
//           Select(Ident(x),weight),
//           TypeTree[TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),class Int)]
//         )
//       )
//     )
//   )
// )





  def generateSelectMultiField(action: FocusAction.SelectMultiField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, toType, enumChildren}

    def generateGetter(from: Term): Term = 
      Select.unique(from, fieldName) // o.field

    def generateAllSetters(from: Term, to: Term): Term = {
      def generateSetter(fromAsCase: Term): Term = {
        //println(fromTypeArgs)
        //println(fromAsCase.tpe.widenTermRefByName.classSymbol.get.memberMethod("copy").map(_.signature))
        Select.overloaded(fromAsCase, "copy", fromTypeArgs, NamedArg(fieldName, to) :: Nil) // o.copy(field = value)
      }

      def generateCaseDef(fromCaseType: TypeRepr): CaseDef = {
        val bindSymbol = Symbol.newBind(Symbol.noSymbol, "x", Flags.EmptyFlags, fromCaseType)

        CaseDef(
          Bind(bindSymbol, Typed(from, TypeIdent(fromCaseType.classSymbol.get))), 
          None, 
          generateSetter(Ref(bindSymbol))) // case x: Branch => x.copy(field = value)
      }
      Match(from, enumChildren.map(generateCaseDef))
    }


// List(
//   CaseDef(
//     Bind(
//       x,
//       Inlined(
//         Ident(SelectMultiFieldGenerator),
//         List(),
//         Ident(from)
//       )
//     ),
//     EmptyTree,
//     Apply(
//       Select(Ident(x),copy),
//       List(NamedArg(weight,Inlined(Ident(SelectMultiFieldGenerator),List(),Ident(to)))))))



    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) => 
        '{
          Lens.apply[f, t]((from: f) => ${ generateGetter('{from}.asTerm).asExprOf[t] })
                          ((to: t) => (from: f) => ${ generateAllSetters('{from}.asTerm, '{to}.asTerm).asExprOf[f] })
        }.asTerm
    }
  }
}
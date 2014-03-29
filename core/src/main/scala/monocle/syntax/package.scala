package monocle

import monocle.syntax._

package object syntax extends Syntax

trait Syntax {

  object lens extends LensSyntax

  object traversal extends TraversalSyntax

  object iso extends IsoSyntax

  object prism extends PrismSyntax

}
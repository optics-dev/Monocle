package monocle
package syntax

trait Syntaxes {

  object lens extends ToLensOps with ToAppliedLensOps

  object traversal extends ToTraversalOps with ToAppliedTraversalOps

}

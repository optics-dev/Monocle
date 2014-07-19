package monocle

import monocle.scalazi._


package object scalazi extends ScalazInstances

trait ScalazInstances
  extends DisjunctionFunctions
  with    IListInstances
  with    OneAndInstances
  with    TreeFunctions with TreeInstances

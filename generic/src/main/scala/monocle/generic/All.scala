package monocle.generic

object all extends GenericInstances

trait GenericInstances
  extends CoProductInstances
  with    HListInstances
  with    ProductOptics
  with    TupleNInstances

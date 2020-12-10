package monocle.generic

@deprecated("no replacement", since = "3.0.0-M1")
object all extends GenericInstances

trait GenericInstances
    extends CoProductInstances
    with HListInstances
    with ProductOptics
    with TupleNInstances
    with GenericOptics

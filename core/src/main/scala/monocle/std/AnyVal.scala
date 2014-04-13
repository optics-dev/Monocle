package monocle.std


trait AnyValInstances extends BooleanInstances
                         with ByteInstances
                         with CharInstances
                         with IntInstances
                         with LongInstances

object anyval extends AnyValInstances

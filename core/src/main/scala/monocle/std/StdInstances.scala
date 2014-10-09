package monocle.std

trait StdInstances
  extends BooleanInstances
  with    ByteInstances
  with    CharInstances
  with    DoubleInstances
  with    StdEitherFunctions
  with    FunctionFunctions with FunctionInstances
  with    IntInstances
  with    ListInstances
  with    LongInstances
  with    MapInstances
  with    MaybeFunctions  with MaybeInstances
  with    OptionFunctions with OptionInstances
  with    SetInstances
  with    StreamInstances
  with    StringInstances
  with    Tuple2Instances
  with    Tuple3Instances
  with    Tuple4Instances
  with    Tuple5Instances
  with    Tuple6Instances
  with    VectorInstances
  // Scalaz Instances
  with    DisjunctionFunctions
  with    TheseFunctions
  with    IListInstances
  with    OneAndInstances
  with    TreeFunctions with TreeInstances

package monocle.std

object all extends StdInstances

trait StdInstances
  extends BigIntOptics
  with    BooleanOptics
  with    ByteOptics
  with    CharOptics
  with    DoubleOptics
  with    EitherOptics
  with    FunctionOptics
  with    IntOptics
  with    ListOptics
  with    LongOptics
  with    MapOptics
  with    MaybeOptics
  with    OptionOptics
  with    SetOptics
  with    StreamOptics
  with    StringOptics
  with    Tuple2Optics
  with    Tuple3Optics
  with    Tuple4Optics
  with    Tuple5Optics
  with    Tuple6Optics
  with    VectorOptics
  // Scalaz Instances
  with    CofreeOptics
  with    Either3Optics
  with    DisjunctionOptics
  with    TheseOptics
  with    IListInstances
  with    IMapOptics
  with    ISetOptics
  with    NonEmptyListOptics
  with    OneAndOptics
  with    TreeOptics
  with    ValidationOptics

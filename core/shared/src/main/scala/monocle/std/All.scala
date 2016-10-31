package monocle.std

object all extends StdInstances

trait StdInstances
  extends BigDecimalOptics
  with    BigIntOptics
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
  with    StringOptics
  with    Tuple1Optics
  // Scalaz Instances
  with    CofreeOptics
  with    Either3Optics
  with    DisjunctionOptics
  with    TheseOptics
  with    IListInstances
  with    IMapOptics
  with    NonEmptyListOptics
  with    TreeOptics
  with    ValidationOptics

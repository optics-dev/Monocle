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
  with    OptionOptics
  with    StringOptics
  with    Tuple1Optics
  with    TryOptics
  // Cats Instances
  with    ChainOptics
  with    TheseOptics
  with    NonEmptyChainOptics
  with    NonEmptyListOptics
  with    NonEmptyVectorOptics
  with    ValidatedOptics

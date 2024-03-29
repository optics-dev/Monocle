package monocle.internal.focus

private[focus] trait ErrorHandling {
  this: FocusBase =>

  def errorReport(error: FocusError): (String, Option[Position]) = error match {
    case FocusError.NotACaseClass(fromClass, fieldName, pos) =>
      (
        s"Cannot generate Lens for field '$fieldName', because '$fromClass' is not a case class",
        Some(pos)
      )
    case FocusError.NotAConcreteClass(fromClass) =>
      (
        s"Expecting a concrete case class in the 'From' position; cannot reify type $fromClass",
        None
      )
    case FocusError.NotASimpleLambdaFunction =>
      (
        s"Expecting a lambda function that directly accesses a field. Example: `Focus[Address](_.streetNumber)`",
        None
      )
    case FocusError.CouldntUnderstandKeywordContext =>
      (
        s"Internal error in monocle.Focus; cannot access special syntax.",
        None
      )
    case FocusError.DidNotDirectlyAccessArgument(argName) =>
      (
        s"Expecting a lambda function that directly accesses the argument; other variable `$argName` found. Example: `Focus[Address](_.streetNumber)`",
        None
      )
    case FocusError.ComposeMismatch(type1, type2) =>
      (
        s"Could not compose $type1.andThen($type2)",
        None
      )
    case FocusError.UnexpectedCodeStructure(code) =>
      (
        s"Unexpected code structure: $code",
        None
      )
    case FocusError.CouldntFindFieldType(fromType, fieldName, pos) =>
      (
        s"Couldn't find type for $fromType.$fieldName",
        Some(pos)
      )
    case FocusError.InvalidDowncast(fromType, toType) =>
      (
        s"Type '$fromType' could not be cast to '$toType'",
        None
      )
  }

}

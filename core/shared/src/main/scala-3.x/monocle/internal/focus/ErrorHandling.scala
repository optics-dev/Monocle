package monocle.internal.focus

private[focus] trait ErrorHandling {
  this: FocusBase => 
  
  def errorMessage(error: FocusError): String = error match {
    case FocusError.NotACaseClass(fromClass, fieldName) => s"Cannot generate Lens for field '$fieldName', because '$fromClass' is not a case class"
    case FocusError.NotAConcreteClass(fromClass) => s"Expecting a concrete case class in the 'From' position; cannot reify type $fromClass"
    case FocusError.NotASimpleLambdaFunction => s"Expecting a lambda function that directly accesses a field. Example: `Focus[Address](_.streetNumber)`"
    case FocusError.CouldntUnderstandKeywordContext => s"Internal error in monocle.Focus; cannot access special syntax."
    case FocusError.DidNotDirectlyAccessArgument(argName) => s"Expecting a lambda function that directly accesses the argument; other variable `$argName` found. Example: `Focus[Address](_.streetNumber)`"
    case FocusError.ComposeMismatch(type1, type2) => s"Could not compose $type1.andThen($type2)"
    case FocusError.UnexpectedCodeStructure(code) => s"Unexpected code structure: $code"
    case FocusError.CouldntFindFieldType(fromType, fieldName) => s"Couldn't find type for $fromType.$fieldName"
    case FocusError.InvalidDowncast(fromType, toType) => s"Type '$fromType' could not be cast to '$toType'"
  }
}
package monocle.internal.focus

private[focus] trait ErrorHandling {
  this: FocusBase => 
  
  def errorMessage(error: FocusError): String = error match {
    case FocusError.NotACaseClass(fromClass) => s"Expecting a case class in the 'From' position; found $fromClass"
    case FocusError.NotAConcreteClass(fromClass) => s"Expecting a concrete case class in the 'From' position; cannot reify type $fromClass"
    case FocusError.NotASimpleLambdaFunction => s"Expecting a lambda function that directly accesses a field. Example: `GenLens[Address](_.streetNumber)`"
    case FocusError.DidNotDirectlyAccessArgument(argName) => s"Expecting a lambda function that directly accesses the argument; other variable `$argName` found. Example: `GenLens[Address](_.streetNumber)`"
    case FocusError.ComposeMismatch(type1, type2) => s"Could not compose $type1 >>> $type2"
    case FocusError.UnexpectedCodeStructure(code) => s"Unexpected code structure: $code"
    case FocusError.CouldntFindFieldType(fromType, fieldName) => s"Couldn't find type for $fromType.$fieldName"
  }
}
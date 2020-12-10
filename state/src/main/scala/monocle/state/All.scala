package monocle.state

@deprecated("no replacement", since = "3.0.0-M1")
object all
    extends StateLensSyntax
    with StateOptionalSyntax
    with StateGetterSyntax
    with StateSetterSyntax
    with StateTraversalSyntax
    with ReaderGetterSyntax

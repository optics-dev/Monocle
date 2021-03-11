package monocle.syntax

import monocle.macros.syntax.{AppliedFocusSyntax, MacroSyntax}

object all extends Syntaxes

trait Syntaxes extends AppliedSyntax with AppliedFocusSyntax with MacroSyntax with FieldsSyntax

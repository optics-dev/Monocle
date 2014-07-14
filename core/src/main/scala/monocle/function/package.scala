package monocle

import monocle.function._


package object function extends Function

trait Function
  extends AtFunctions
  with    AtBitFunctions
  with    CurryFunctions
  with    EachFunctions
  with    Field1Functions
  with    Field2Functions
  with    Field3Functions
  with    Field4Functions
  with    Field5Functions
  with    Field6Functions
  with    FilterIndexFunctions
  with    HeadFunctions
  with    HeadOptionFunctions
  with    IndexFunctions
  with    InitFunctions
  with    InitOptionFunctions
  with    LastFunctions
  with    LastOptionFunctions
  with    ReverseFunctions
  with    SafeCastFunctions
  with    TailFunctions
  with    TailOptionFunctions
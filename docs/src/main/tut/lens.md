---
layout: default
title:  "Lens"
section: "optics"
scaladoc: "http://julien-truffaut.github.io/Monocle/api/#monocle.PLens"
source: "https://github.com/julien-truffaut/Monocle/blob/master/example/src/main/scala/monocle/example/LensExample.scala"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/lens.md"
---
# Lens

A `Lens` is an Optic used to zoom inside a `Product`, e.g. `case class`, `Tuple`, `HList`.

`Lenses` have two type parameters generally called `S` and `A`: `Lens[S, A]` where `S` represents the `Product` and `A` an element inside of `S`.
`Lenses` are defined by a pair functions: `get` and `set`. 

```scala
object Lens {
  def apply[S, A](get: S => A)(set: A => S => S): Lens[S, A]
}
```

## Laws 

```tut:silent
import monocle.Lens

class LensLaws[S, A](lens: Lens[S, A]) {

  def getSetLaw(s: S): Boolean =
    lens.set(lens.get(s)) == s
    
  def setGetLaw(s: S, a: A): Boolean =
    lens.get(lens.set(a)(s)) == a
    
}
```

`getSetLaw` states that if you `get` a value `A` from `S` and then `set` it back in, the result is an object identical to the original one. 
A side effect of this law is that `set` is constraint to only update the `A` it points to, for example it cannot 
increment a counter or modify another value of type `A`.

`setGetLaw` states that if you `set` a value, you always `get` the same value back. This law guarantees that `set` is
 actually updating a value of type `A`.

## Examples and derived methods

Let's take a simple case class:

```scala
case class Address(streetNumber: Int, streetName: String)
```

`Address` is a `Product` of two fields `streetNumber` and `streetName`. So we can define a `Lens` from `Address` to `Int`
that will zoom from an `Address` to its field `streetNumber`. To create such `Lens` we need two functions:
*   `get: Address => Int`
*   `set: Int => Address => Address` 

```tut
import monocle.example.LensExample._

def get(address: Address): Int =
  address.streetNumber
  
def set(newNo: Int)(address: Address): Address =
  address.copy(streetNumber = newNo)  
  
val _streetNumber = Lens[Address, Int](get)(set)
```

Or directly,

```tut
val _streetNumber2 = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
```

TODO

```tut
import monocle.Lens
import monocle.example.LensExample._

john

_age: Lens[Person, Int]

_age.get(john)
_age.set(25)(john)
_age.modify(_ + 1)(john)
```

Lenses can be composed to zoom deeper in a data structure

```tut
(_address composeLens _streetNumber).get(john)
(_address composeLens _streetNumber).set(2)(john)
```




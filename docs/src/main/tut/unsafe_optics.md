---
layout: default
title:  "Unsafe Optics"
section: "optics"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/unsafe_optics.md"
---
# Background

All the Optics defined in the `core` module obey a well defined set of `Laws` that make these Optics safe to use for all possible cases.
Some Optics however cannot make such guarantees and as a result are said to be "unsafe" and placed in the `unsafe` module. 

This is not to say that these Optics cannot be used - they actually come in handy on many occasions - but care must be taken in using them as we will proceed show in the next sections.


# The `unsafe` module

Unsafe Optics are defined in the `unsafe` module.  This module contains the following Optics:

- UnsafeSelect
- UnsafeHCompose


## UnsafeSelect

Cesar this section needs your help :)



## UnsafeHCompose

`UnsafeHCompose` offers the ability to work with *any number* (0 to n) of `Lens` on `S`.  

It is a special case of `Traversal` that is considered unsafe because the (0 to n) of `Lens` requirement breaks the xxxx `Law` of `Traversal`.


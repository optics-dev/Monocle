---
layout: docs
section: "optics_menu"
title:  "Optics"
position: 2
---
## Optics

Here is a diagram to visualise how optics relate to each other:

![Class Diagram](https://raw.github.com/julien-truffaut/Monocle/master/image/class-diagram.png)

## Optic Composition Table

Almost all optics compose with the other optics. The composition of one type and itself results in the same type of optic.

|               | Getter     | Iso        | Lens       | Optional     | Prism      | Setter     | Traversal     |
| ------------- |:----------:|:----------:|:----------:|:------------:|:----------:|:----------:|:-------------:|
| **Getter**    | **Getter** | Getter     | Getter     | Fold         | Fold       | -          | Fold          |
| **Iso**       | Getter     | **Iso**    | Lens       | Optional     | Prism      | Setter     | Traversal     | 
| **Lens**      | Getter     | Lens       | **Lens**   | Optional     | Optional   | Setter     | Traversal     |
| **Optional**  | Fold       | Optional   | Optional   | **Optional** | Optional   | Setter     | Traversal     |
| **Prism**     | Fold       | Prism      | Optional   | Optional     | **Prism**  | Setter     | Traversal     |
| **Setter**    | -          | Setter     | Setter     | Setter       | Setter     | **Setter** | Setter        |
| **Traversal** | Fold       | Traversal  | Traversal  | Traversal    | Traversal  | Setter     | **Traversal** |

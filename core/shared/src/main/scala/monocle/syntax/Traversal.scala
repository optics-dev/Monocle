package monocle.syntax

import scalaz.{IndexedStore, FreeAp}

object traversal extends TraversalSyntax

trait TraversalSyntax {
  implicit def toTraversalBuilder[A, B, T](a: A): TraversalBuilder[A, B, T] =
    new TraversalBuilder[A, B, T] { val a1 = a }
}

trait TraversalBuilder[A, B, T] {
  val a1: A

  def apply(f: B => T) =
    ap(a1, pt(f))

  def ~(_a2: A) = new TraversalBuilder2 { val a2 = _a2 }

  trait TraversalBuilder2 {
    val a2: A

    def apply(f: (B, B) => T) =
      ap(a2, ap(a1, pt(f.curried)))

    def ~(_a3: A) = new TraversalBuilder3 { val a3 = _a3 }

    trait TraversalBuilder3 {
      val a3: A

      def apply(f: (B, B, B) => T) =
        ap(a3, ap(a2, ap(a1, pt(f.curried))))

      def ~(_a4: A) = new TraversalBuilder4 { val a4 = _a4 }

      trait TraversalBuilder4 {
        val a4: A

        def apply(f: (B, B, B, B) => T) =
          ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried)))))

        def ~(_a5: A) = new TraversalBuilder5 { val a5 = _a5 }

        trait TraversalBuilder5 {
          val a5: A

          def apply(f: (B, B, B, B, B) => T) =
            ap(a5, ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried))))))

          def ~(_a6: A) = new TraversalBuilder6 { val a6 = _a6 }

          trait TraversalBuilder6 {
            val a6: A

            def apply(f: (B, B, B, B, B, B) => T) =
              ap(a6, ap(a5, ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried)))))))

            def ~(_a7: A) = new TraversalBuilder7 { val a7 = _a7 }

            trait TraversalBuilder7 {
              val a7: A

              def apply(f: (B, B, B, B, B, B, B) => T) =
                ap(a7, ap(a6, ap(a5, ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried))))))))

              def ~(_a8: A) = new TraversalBuilder8 { val a8 = _a8 }

              trait TraversalBuilder8 {
                val a8: A

                def apply(f: (B, B, B, B, B, B, B, B) => T) =
                  ap(a8, ap(a7, ap(a6, ap(a5, ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried)))))))))

                def ~(_a9: A) = new TraversalBuilder9 { val a9 = _a9 }

                trait TraversalBuilder9 {
                  val a9: A

                  def apply(f: (B, B, B, B, B, B, B, B, B) => T) =
                    ap(a9, ap(a8, ap(a7, ap(a6, ap(a5, ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried))))))))))

                  def ~(_a10: A) = new TraversalBuilder10 { val a10 = _a10 }

                  trait TraversalBuilder10 {
                    val a10: A

                    def apply(f: (B, B, B, B, B, B, B, B, B, B) => T) =
                      ap(a10, ap(a9, ap(a8, ap(a7, ap(a6, ap(a5, ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried)))))))))))

                    def ~(_a11: A) = new TraversalBuilder11 { val a11 = _a11 }

                    trait TraversalBuilder11 {
                      val a11: A

                      def apply(f: (B, B, B, B, B, B, B, B, B, B, B) => T) =
                        ap(a11, ap(a10, ap(a9, ap(a8, ap(a7, ap(a6, ap(a5, ap(a4, ap(a3, ap(a2, ap(a1, pt(f.curried))))))))))))
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private def pt[X](x: X) = FreeAp.point[IndexedStore[A, B, ?], X](x)

  private def ap[X](h: A, x: => FreeAp[IndexedStore[A, B, ?], B => X]) =
    FreeAp[IndexedStore[A, B, ?], B, X](IndexedStore(identity, h), x)
}

package monocle

final class CompositionConcreteOpticsTest extends munit.FunSuite {

  val iso: Iso[Int, Int]             = Iso.id
  val lens: Lens[Int, Int]           = Lens.id
  val prism: Prism[Int, Int]         = Prism.id
  val optional: Optional[Int, Int]   = Optional.id
  val traversal: Traversal[Int, Int] = Traversal.id
  val setter: Setter[Int, Int]       = Setter.id
  val getter: Getter[Int, Int]       = Getter.id
  val fold: Fold[Int, Int]           = Fold.id

  test("iso . iso") {
    val optic = iso.andThen(iso)
    optic: Iso[Int, Int]
  }

  test("iso . lens") {
    val optic = iso.andThen(lens)
    optic: Lens[Int, Int]
  }

  test("iso . prism") {
    val optic = iso.andThen(prism)
    optic: Prism[Int, Int]
  }

  test("iso . optional") {
    val optic = iso.andThen(optional)
    optic: Optional[Int, Int]
  }

  test("iso . traversal") {
    val optic = iso.andThen(traversal)
    optic: Traversal[Int, Int]
  }

  test("iso . setter") {
    val optic = iso.andThen(setter)
    optic: Setter[Int, Int]
  }

  test("iso . getter") {
    val optic = iso.andThen(getter)
    optic: Getter[Int, Int]
  }

  test("iso . fold") {
    val optic = iso.andThen(fold)
    optic: Fold[Int, Int]
  }

  test("lens . iso") {
    val optic = lens.andThen(iso)
    optic: Lens[Int, Int]
  }

  test("lens . lens") {
    val optic = lens.andThen(lens)
    optic: Lens[Int, Int]
  }

  test("lens . prism") {
    val optic = lens.andThen(prism)
    optic: Optional[Int, Int]
  }

  test("lens . optional") {
    val optic = lens.andThen(optional)
    optic: Optional[Int, Int]
  }

  test("lens . traversal") {
    val optic = lens.andThen(traversal)
    optic: Traversal[Int, Int]
  }

  test("lens . setter") {
    val optic = lens.andThen(setter)
    optic: Setter[Int, Int]
  }

  test("lens . getter") {
    val optic = lens.andThen(getter)
    optic: Getter[Int, Int]
  }

  test("lens . fold") {
    val optic = lens.andThen(fold)
    optic: Fold[Int, Int]
  }

  test("prism . iso") {
    val optic = prism.andThen(iso)
    optic: Prism[Int, Int]
  }

  test("prism . lens") {
    val optic = prism.andThen(lens)
    optic: Optional[Int, Int]
  }

  test("prism . prism") {
    val optic = prism.andThen(prism)
    optic: Prism[Int, Int]
  }

  test("prism . optional") {
    val optic = prism.andThen(optional)
    optic: Optional[Int, Int]
  }

  test("prism . traversal") {
    val optic = prism.andThen(traversal)
    optic: Traversal[Int, Int]
  }

  test("prism . setter") {
    val optic = prism.andThen(setter)
    optic: Setter[Int, Int]
  }

  test("prism . getter") {
    val optic = prism.andThen(getter)
    optic: Fold[Int, Int]
  }

  test("prism . fold") {
    val optic = prism.andThen(fold)
    optic: Fold[Int, Int]
  }

  test("optional . iso") {
    val optic = optional.andThen(iso)
    optic: Optional[Int, Int]
  }

  test("optional . lens") {
    val optic = optional.andThen(lens)
    optic: Optional[Int, Int]
  }

  test("optional . prism") {
    val optic = optional.andThen(prism)
    optic: Optional[Int, Int]
  }

  test("optional . optional") {
    val optic = optional.andThen(optional)
    optic: Optional[Int, Int]
  }

  test("optional . traversal") {
    val optic = optional.andThen(traversal)
    optic: Traversal[Int, Int]
  }

  test("optional . setter") {
    val optic = optional.andThen(setter)
    optic: Setter[Int, Int]
  }

  test("optional . getter") {
    val optic = optional.andThen(getter)
    optic: Fold[Int, Int]
  }

  test("optional . fold") {
    val optic = optional.andThen(fold)
    optic: Fold[Int, Int]
  }

  test("traversal . iso") {
    val optic = traversal.andThen(iso)
    optic: Traversal[Int, Int]
  }

  test("traversal . lens") {
    val optic = traversal.andThen(lens)
    optic: Traversal[Int, Int]
  }

  test("traversal . prism") {
    val optic = traversal.andThen(prism)
    optic: Traversal[Int, Int]
  }

  test("traversal . optional") {
    val optic = traversal.andThen(optional)
    optic: Traversal[Int, Int]
  }

  test("traversal . traversal") {
    val optic = traversal.andThen(traversal)
    optic: Traversal[Int, Int]
  }

  test("traversal . setter") {
    val optic = traversal.andThen(setter)
    optic: Setter[Int, Int]
  }

  test("traversal . getter") {
    val optic = traversal.andThen(getter)
    optic: Fold[Int, Int]
  }

  test("traversal . fold") {
    val optic = traversal.andThen(fold)
    optic: Fold[Int, Int]
  }

  test("setter . iso") {
    val optic = setter.andThen(iso)
    optic: Setter[Int, Int]
  }

  test("setter . lens") {
    val optic = setter.andThen(lens)
    optic: Setter[Int, Int]
  }

  test("setter . prism") {
    val optic = setter.andThen(prism)
    optic: Setter[Int, Int]
  }

  test("setter . optional") {
    val optic = setter.andThen(optional)
    optic: Setter[Int, Int]
  }

  test("setter . traversal") {
    val optic = setter.andThen(traversal)
    optic: Setter[Int, Int]
  }

  test("setter . setter") {
    val optic = setter.andThen(setter)
    optic: Setter[Int, Int]
  }

  test("error setter . getter") {
    compileErrors("setter.andThen(getter)")
  }

  test("error setter . fold") {
    compileErrors("setter.andThen(fold)")
  }

  test("getter . iso") {
    val optic = getter.andThen(iso)
    optic: Getter[Int, Int]
  }

  test("getter . lens") {
    val optic = getter.andThen(lens)
    optic: Getter[Int, Int]
  }

  test("getter . prism") {
    val optic = getter.andThen(prism)
    optic: Fold[Int, Int]
  }

  test("getter . optional") {
    val optic = getter.andThen(optional)
    optic: Fold[Int, Int]
  }

  test("getter . traversal") {
    val optic = getter.andThen(traversal)
    optic: Fold[Int, Int]
  }

  test("error getter . setter") {
    compileErrors("getter.andThen(setter)")
  }

  test("getter . getter") {
    val optic = getter.andThen(getter)
    optic: Getter[Int, Int]
  }

  test("getter . fold") {
    val optic = getter.andThen(fold)
    optic: Fold[Int, Int]
  }

  test("fold . iso") {
    val optic = fold.andThen(iso)
    optic: Fold[Int, Int]
  }

  test("fold . lens") {
    val optic = fold.andThen(lens)
    optic: Fold[Int, Int]
  }

  test("fold . prism") {
    val optic = fold.andThen(prism)
    optic: Fold[Int, Int]
  }

  test("fold . optional") {
    val optic = fold.andThen(optional)
    optic: Fold[Int, Int]
  }

  test("fold . traversal") {
    val optic = fold.andThen(traversal)
    optic: Fold[Int, Int]
  }

  test("error fold . setter") {
    compileErrors("fold.andThen(setter)")
  }

  test("fold . getter") {
    val optic = fold.andThen(getter)
    optic: Fold[Int, Int]
  }

  test("fold . fold") {
    val optic = fold.andThen(fold)
    optic: Fold[Int, Int]
  }

}

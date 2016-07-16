package monocle.unsafe

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

/**
  * Created by Cesar on 16/07/2016.
  */
class UnsafeSelectSpec extends MonocleSuite {
  /*
    This fails the "unsafe.Prism.round trip other way" test with value -1
    checkAll("unsafe", PrismTests(UnsafeSelect.unsafeSelect((a: Int) => a > Int.MaxValue / 2)))
   */
}

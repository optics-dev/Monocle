package monocle


class RegressionSpec extends MonocleSuite {

  test("#244 - String to Long: '-' to long should not succeed.") {
    stringToLong.getOption("-") shouldEqual None
  }

}

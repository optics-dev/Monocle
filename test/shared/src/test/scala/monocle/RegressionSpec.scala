package monocle

class RegressionSpec extends MonocleSuite {

  test("#244 - String to Long: '-' to long should not succeed.") {
    stringToLong.getOption("-") shouldEqual None
  }

  test("#336 - String to Long: '001' to long should not succeed.") {
    stringToLong.modifyOption(identity)("001") shouldEqual None
    stringToLong.modify(identity)("001") shouldEqual "001"
  }

  test("#336 - String to Long: '0' should succeed.") {
    stringToLong.modifyOption(identity)("0") shouldEqual Some("0")
  }

  test("#336 - Uppercase booleans not obeying Prism laws") {
    stringToBoolean.modifyOption(identity)("TRUE") should be (None)
    stringToBoolean.modifyOption(identity)("False") should be (None)

    stringToBoolean.modify(identity)("true") should be ("true")
    stringToBoolean.modify(identity)("false") should be ("false")
  }

}

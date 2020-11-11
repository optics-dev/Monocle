package monocle

class RegressionSpec extends MonocleSuite {
  test("#244 - String to Long: '-' to long should not succeed.") {
    assertEquals(stringToLong.getOption("-"), None)
  }

  test("#336 - String to Long: '001' to long should not succeed.") {
    assertEquals(stringToLong.modifyOption(identity)("001"), None)
    assertEquals(stringToLong.modify(identity)("001"), "001")
  }

  test("#336 - String to Long: '0' should succeed.") {
    assertEquals(stringToLong.modifyOption(identity)("0"), Some("0"))
  }

  test("#336 - Uppercase booleans not obeying Prism laws") {
    assertEquals(stringToBoolean.modifyOption(identity)("TRUE"), None)
    assertEquals(stringToBoolean.modifyOption(identity)("False"), None)

    assertEquals(stringToBoolean.modify(identity)("true"), "true")
    assertEquals(stringToBoolean.modify(identity)("false"), "false")
  }
}

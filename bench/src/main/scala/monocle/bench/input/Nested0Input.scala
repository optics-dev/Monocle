package monocle.bench.input

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Scope, Setup, State}

@State(Scope.Thread)
class Nested0Input extends InputHelper {

  var n0: Nested0 = _

  @Setup
  def setup(): Unit =
    n0 = Nested0("zgdhlhmjrztahopqdkgqigi",-327920680,
      Nested1("qtpdkqfeliwdfaquuxneawgrgscxfdnexsqxwixpkwwvpshowte",1124829713,
        Nested2("tysehjufjlehezmchgritmzpgbypsjbpkasynru",-1072495572,
          Nested3("aptbtwqnielouhrabgwugtstrjbrlovywpgajhboadgmjsjmdvhcmehmdwaliaztuzvgjglkuwmengeg",918872300,
            Nested4("ejvtwdcchcuwaadcegguuerv",-1149022154,
              Nested5("vlqtfxtthbrepthcpxoxrocparbvdqdgkvbmfsrlywxdsynyeibgygaipzkmdmsmuyanruxxoirmuerc",1480324764,
                Nested6("hewgyqkkvaoheuayvzelehxwwnxzoptiqsghhhxhhdqxfrtfsntokaobauavbtifmc",726619256)
                ,-5714776854542260210L),5128304677366840630L),9010743111940277888L),-636375720643769063L),5296381550469435162L),-6070111082840969110L)

}

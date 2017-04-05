package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {
  test("locateTemperatures works") {
    val located1975 = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")

    located1975.take(1).map{ tuple =>
      //println(tuple)
      assert(tuple._1.toString == "1975-01-01")
      assert(tuple._2.toString == "Location(70.933,-8.667)")
      assert(tuple._3 == 23.2)
    }
  }
  
}
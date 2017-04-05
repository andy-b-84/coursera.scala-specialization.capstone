package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {
  private val located1975 = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")

  test("locateTemperatures works") {
    //located1975.take(10).foreach{t => println(t)}

    val tuple = located1975.head
    //println(tuple)
    assert(tuple._1.toString == "1975-01-01")
    assert(tuple._2.toString == "Location(70.933,-8.667)")
    assert(tuple._3 == -4.888888888888889)
  }

  test("locationYearlyAverageRecords works") {
    val meaned1975 = Extraction.locationYearlyAverageRecords(located1975)

    //meaned1975.take(10).foreach{t => println(t)}

    assert(meaned1975.head._1.toString == "Location(67.55,-63.783)")
    assert(meaned1975.head._2.round == -6.6544511378848865.round)
  }
  
}
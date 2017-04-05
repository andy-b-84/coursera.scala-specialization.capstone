package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {
  test("locateTemperatures works") {
    val located1975 = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")

    //located1975.take(10).foreach{t => println(t)}

    val tuple = located1975.head
    //println(tuple)
    assert(tuple._1.toString == "1975-01-01")
    assert(tuple._2.toString == "Location(70.933,-8.667)")
    assert(tuple._3 == -4.888888888888889)
  }

  /*[Test Description] [#1 - Data extraction] compute yearly average by location
[Observed Error] an implementation is missing
[exception was thrown] detailed error message in debug output section below
[Lost Points] 5

[Test Description] [#1 - Data extraction] locationYearlyAverageRecords should be able to process 1 million records
[Observed Error] an implementation is missing
[Lost Points] 5

*/
  
}
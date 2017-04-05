package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {
  test("locateTemperatures works") {
    val located1975 = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")

    located1975.take(10).foreach{t => println(t)}

    val tuple = located1975.head
    //println(tuple)
    assert(tuple._1.toString == "1975-01-01")
    assert(tuple._2.toString == "Location(70.933,-8.667)")
    assert(tuple._3 == 23.2)
  }

  //test("[Test Description] [#1 - Data extraction] weather stations are identified by the composite (STN, WBAN)")

  //test("[Test Description] [#1 - Data extraction] stations with no location are ignored")

  /*[Test Description] [#1 - Data extraction] compute yearly average by location
[Observed Error] an implementation is missing
[exception was thrown] detailed error message in debug output section below
[Lost Points] 5

[Test Description] [#1 - Data extraction] locationYearlyAverageRecords should be able to process 1 million records
[Observed Error] an implementation is missing
[Lost Points] 5

[Test Description] [#1 - Data extraction] weather stations are identified by the composite (STN, WBAN)
[Observed Error]
 Set(
  (2000-01-05,Location(5.0,-5.0),50.0),
  (2000-01-04,Location(4.0,-4.0),50.0),
  (2000-01-01,Location(1.0,-1.0),50.0),
  (2000-01-02,Location(2.0,-2.0),50.0),
  (2000-01-03,Location(3.0,-3.0),50.0))
  did not equal
  Set(
  (2000-01-03,Location(3.0,-3.0),10.0),
  (2000-01-02,Location(2.0,-2.0),10.0),
  (2000-01-01,Location(1.0,-1.0),10.0),
  (2000-01-05,Location(5.0,-5.0),10.0),
  (2000-01-04,Location(4.0,-4.0),10.0))
[Lost Points] 3

[Test Description] [#1 - Data extraction] temperatures are located
[Observed Error] (2000-01-01,Location(1.0,-1.0),50.0) did not equal (2000-01-01,Location(1.0,-1.0),10.0)
[Lost Points] 5

[Test Description] [#1 - Data extraction] stations with no location are ignored
[Observed Error] List((2000-01-01,Location(0.0,0.0),50.0)) was not empty
[Lost Points] 3

*/
  
}
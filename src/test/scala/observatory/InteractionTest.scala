package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import Interaction._

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {

  test("[#3 - Interactive visualization] tileLocation must return the corresponding latitude and longitude, given some Web Mercator coordinates") {
    val loc = tileLocation(10, 512, 384) // Shows somewhere NW of Valencia, Spain, the top-left GPS coordinates should be on Valderrobres
    //println(loc)
    assert(loc.lat.toInt == 40)
    assert(loc.lon.toInt == 0)
  }
  /*
  * [Test Description] [#3 - Interactive visualization] tileLocation must return the corresponding latitude and longitude, given some Web Mercator coordinates
[Observed Error] an implementation is missing
[exception was thrown] detailed error message in debug output section below
[Lost Points] 2

  *
  *
  * [Test Description] [#3 - Interactive visualization] tile must be consistent accross zoom levels
[Observed Error] an implementation is missing
[exception was thrown] detailed error message in debug output section below
[Lost Points] 3

[Test Description] [#3 - Interactive visualization] generateTiles covers all the expected tiles
[Observed Error] an implementation is missing
[exception was thrown] detailed error message in debug output section below
[Lost Points] 3

[Test Description] [#3 - Interactive visualization] tile pixel colors must be consistent with the given located temperatures and color scale
[Observed Error] NotImplementedError was thrown during property evaluation.
  Message: an implementation is missing
  Occurred when passed generated values (
    arg0 = true
  )
[Lost Points] 5
  * */
}

package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import Interaction._
import com.sksamuel.scrimage.RGBColor
import observatory.Test._

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {

  test("[#3 - Interactive visualization] tileLocation must return the corresponding latitude and longitude, given some Web Mercator coordinates") {
    val loc = tileLocation(10, 512, 384) // Shows somewhere NW of Valencia, Spain, the top-left GPS coordinates should be on Valderrobres
    //println(loc)
    assert(loc.lat.toInt == 40)
    assert(loc.lon.toInt == 0)
  }

  test("[#3 - Interactive visualization] tile pixel colors must be consistent with the given located temperatures and color scale") {
    val image = tile(Map(
      Location(0.0, 40.979898069620134) -> 10.0,
      Location(0.0, 41.22794112992287) -> 20.0,
      Location(0.2490234375, 40.979898069620134) -> 30.0,
      Location(0.2490234375, 41.22794112992287) -> 40.0
    ), scale, 10, 512, 384)

    image.output(new java.io.File(s"target/test.png"))

    assert(image.color(0,0) == RGBColor(255,222,0,127))
  }
  /*
  * [Test Description] [#3 - Interactive visualization] tile must be consistent accross zoom levels
[Observed Error] 219.91361940543837 was not less than 30
[Lost Points] 3

[Test Description] [#3 - Interactive visualization] tile pixel colors must be consistent with the given located temperatures and color scale
[Observed Error] GeneratorDrivenPropertyCheckFailedException was thrown during property evaluation.
 (InteractionTest.scala:34)
  Falsified after 0 successful property evaluations.
  Location: (InteractionTest.scala:34)
  Occurred when passed generated values (
    arg0 = true
  )
  Label of failing property:
    Incorrect computed color at Location(63.54855223203643,-180.0): Color(118,0,137). Expected to be closer to Color(255,0,0) than Color(0,0,255)
[Lost Points] 5

[Test Description] [#3 - Interactive visualization] generateTiles covers all the expected tiles
[Observed Error] an implementation is missing
[exception was thrown] detailed error message in debug output section below
[Lost Points] 3
  * */
}

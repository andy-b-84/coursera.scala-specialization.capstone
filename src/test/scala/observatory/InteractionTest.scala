package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import Interaction._
import com.sksamuel.scrimage.RGBColor

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {

  test("[#3 - Interactive visualization] tileLocation must return the corresponding latitude and longitude, given some Web Mercator coordinates") {
    val loc = tileLocation(10, 512, 384) // Shows somewhere NW of Valencia, Spain, the top-left GPS coordinates should be on Valderrobres
    //println(loc)
    assert(loc.lat.toInt == 40)
    assert(loc.lon.toInt == 0)
  }

  test ("[#3 - Interactive visualization] tile must be consistent accross zoom levels") {
    val image = tile(Map(
      Location(40.979898069620134, -10) -> -50.0,
      Location(40.979898069620134, 10) -> 50.0
    ), Map(
      -50.0 -> Color(0, 0, 255),
      50.0 -> Color(255, 0, 0)
    ), 0, 0, 0)

    //image.output(new java.io.File(s"target/test.png"))

    assert(image.color(0,0) == RGBColor(128,0,128,127))
  }

  test("[#3 - Interactive visualization] tile pixel colors must be consistent with the given located temperatures and color scale") {
    (-180.0, -179.6484375, -27.05912578437406, -26.745610382199015)
    val image = tile(Map(
      Location(-26.745610382199015, 180.0) -> 10.0,
      Location(-26.745610382199015, -179.6484375) -> 20.0,
      Location(-27.05912578437406, -179.6484375) -> 30.0,
      Location(-27.05912578437406, 180.0) -> 40.0
    ), Map(
      10.0 -> Color(0, 0, 255),
      20.0 -> Color(0, 255, 255),
      30.0 -> Color(255, 255, 0),
      40.0 -> Color(255, 0, 0)
    ), 8, 0, 147)
    //), 9, 0, 295)
    //), 10, 0, 591)

    //image.output(new java.io.File(s"target/test.png"))

    assert(image.color(0,0) == RGBColor(0,0,255,127))
    assert(image.color(255,0) == RGBColor(0,255,255,127))
    assert(image.color(255,255) == RGBColor(255,255,0,127))
    assert(image.color(0,255) == RGBColor(255,0,0,127))
  }
  /*
  * [Test Description] [#3 - Interactive visualization] tile pixel colors must be consistent with the given located temperatures and color scale
[Observed Error] GeneratorDrivenPropertyCheckFailedException was thrown during property evaluation.
 (InteractionTest.scala:34)
  Falsified after 0 successful property evaluations.
  Location: (InteractionTest.scala:34)
  Occurred when passed generated values (
    arg0 = false
  )
  Label of failing property:
    Incorrect computed color at Location(-27.05912578437406,-180.0): Color(187,0,68). Expected to be closer to Color(0,0,255) than Color(255,0,0)
[Lost Points] 5
  *
  * [[Test Description] [#3 - Interactive visualization] generateTiles covers all the expected tiles
[Observed Error] an implementation is missing
[exception was thrown] detailed error message in debug output section below
[Lost Points] 3
  * */
}

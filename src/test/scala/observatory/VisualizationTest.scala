package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import Visualization._

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {
  private val scale: Map[Double, Color] = Map(
    1.0 -> Color(255,0,0),
    77.68977037419594 -> Color(0,0,255)
  )

  test("[#2 - Raw data display] exceeding the greatest value of a color scale should return the color associated with the greatest value") {
    val c = interpolateColor(scale, -9)

    assert(c == Color(255,0,0), s"Incorrect predicted color: $c. Expected: Color(255,0,0)")
  }

  test("[#2 - Raw data display] basic color interpolation") {
    val c = interpolateColor(scale, 39.34488518709797)
    assert(c == Color(128,0,128))
  }

  test("[#2 - Raw data display] color interpolation") {
    val localScale:Map[Double, Color] = Map(
      -2.147483648E9 -> Color(255,0,0),
      2.147483647E9  -> Color(0,0,255)
    )
    val c = interpolateColor(localScale, -1.07374182425E9)
    assert(c == Color(191,0,64))
  }

  test("[#2 - Raw data display] predicted temperature at location z should be closer to known temperature at location x than to known temperature at location y, if z is closer (in distance) to x than y, and vice versa") {
    val distrib = Map(
      Location(45,0) -> 0.0,
      Location(45,40) -> 40.0
    )

    val predictedTemperature1 = predictTemperature(distrib, Location(45,10))
    val predictedTemperature2 = predictTemperature(distrib, Location(45,20))
    val predictedTemperature3 = predictTemperature(distrib, Location(45,30))

    assert(predictedTemperature1 < 10)
    assert(Math.round(predictedTemperature2) == 20)
    assert(predictedTemperature3 > 30)
  }

/*
[Test Description] [#2 - Raw data display] visualize
[Observed Error] ExecutionException was thrown during property evaluation.
  Message: Boxed Error
  Occurred when passed generated values (
    arg0 = 19.677803706030716,
    arg1 = 71.73764235976873
  )
[Lost Points] 5

[Test Description] [#2 - Raw data display] visualize
[Observed Error] IllegalArgumentException was thrown during property evaluation.
  Message: requirement failed
  Occurred when passed generated values (
    arg0 = 0.0,
    arg1 = -48.36540540380187
  )
[Lost Points] 5

*/

}

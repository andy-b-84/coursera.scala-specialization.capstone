package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import Visualization._
import observatory.Test._

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
    assert(predictedTemperature2 > 19)
    assert(predictedTemperature2 < 21)
    assert(predictedTemperature3 > 30)
  }

  test("[#2 - Raw data display] visualize") {
    val predictedTemperature1 = predictTemperature(distrib, Location(0,-5))
    val predictedTemperature2 = predictTemperature(distrib, Location(5,0))
    val predictedTemperature3 = predictTemperature(distrib, Location(0,5))
    val predictedTemperature4 = predictTemperature(distrib, Location(-5,0))
    val predictedTemperature5 = predictTemperature(distrib, Location(0,0))

    assert(predictedTemperature1 > -1)
    assert(predictedTemperature1 < 1)
    assert(predictedTemperature2 > 14)
    assert(predictedTemperature2 < 16)
    assert(predictedTemperature3 > 24)
    assert(predictedTemperature3 < 26)
    assert(predictedTemperature4 > 9)
    assert(predictedTemperature4 < 11)
    assert(predictedTemperature5 > 7)
    assert(predictedTemperature5 < 13)

  }

}

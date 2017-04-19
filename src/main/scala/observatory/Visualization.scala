package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.language.postfixOps

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val distancePower = 5

    def greatCircleDistanceAngle(location1: Location, location2: Location): Angle = {
      val phi1 = Math.toRadians(location1.lat)
      val lambda1 = Math.toRadians(location1.lon)
      val phi2 = Math.toRadians(location2.lat)
      val lambda2 = Math.toRadians(location2.lon)

      val deltaLambda = Math.abs(lambda1 - lambda2)

      val result = Math.atan2(
        Math.sqrt(
          Math.pow(
            Math.cos(phi2) * Math.sin(deltaLambda)
            , 2
          ) +
          Math.pow (
            (Math.cos(phi1)*Math.sin(phi2)) -
            (Math.sin(phi1)*Math.cos(phi2)*Math.cos(deltaLambda))
            , 2
          )
        ),
        (Math.sin(phi1)*Math.sin(phi2)) +
        (Math.cos(phi1)*Math.cos(phi2)*Math.cos(deltaLambda))
      )
      if (result.isNaN) {
        //Math.acos has results for angles between -pi and pi, both excluded, thus returns NaN if one tries to calculate
        // pi or -pi. Thus this result here.
        Math.PI
      }
      else result
    }

    val temperaturesMap = temperatures.toMap
    if (temperaturesMap.isDefinedAt(location)) {
      //println(s"[Visualization.predictTemperature] found t° for $location")
      temperaturesMap(location)
    }
    else {
      val set = temperaturesMap.map{ tuple => (tuple._1, (greatCircleDistanceAngle(location, tuple._1), tuple._2)) }
      //var debug = ""
      val result = set.aggregate(0.0:Temperature)((acc, tuple) => {
        val secondTuple = tuple._2
        val r = acc + (secondTuple._2/Math.pow(secondTuple._1, distancePower))
        //debug = debug + s"[Visualization.predictTemperature] for location $location : (acc, tuple) = ($acc, $tuple), newAcc = $r\n"
        r
      }, _+_) /
        set.aggregate(0.0:Temperature)((acc, tuple) => {acc + (1/Math.pow(tuple._2._1, distancePower))}, _+_)

      //println(debug)
      //println(s"[Visualization.predictTemperature] location = $location , result = $result")

      result
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    val pointsMap = points.toMap
    if (pointsMap.isDefinedAt(value)) pointsMap(value)
    else {
      def interpolateChannel(cMin: Channel, cMax: Channel, delta: Double): Channel =
        cMin + Math.round((cMax-cMin) * delta).toInt

      def interpolate(min:(Temperature, Color), max:(Temperature, Color)): Color = {
        val delta = (value - min._1) / (max._1 - min._1)
        Color(
          interpolateChannel(min._2.red, max._2.red, delta),
          interpolateChannel(min._2.green, max._2.green, delta),
          interpolateChannel(min._2.blue, max._2.blue, delta)
        )
      }

      val result = points.toList.partition(_._1<=value) match {
        case (Nil, max) => max.head._2
        case (min, Nil) => min.last._2
        case (min, max) => interpolate(min.last, max.head)
      }

      //println(s"interpolateColor($value) = $result")

      result
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val sortedColors = colors.toList.sortBy(_._1)

    val colorsA = Seq.range(0, 360*180).par.map { arrayIndex =>
      val x = arrayIndex % 360
      val y = (arrayIndex - x) / 360

      val lon = x - 180
      val lat = 90 - y

      val color = interpolateColor(sortedColors, predictTemperature(temperatures, Location(lat.toDouble, lon.toDouble)))

      Pixel(color.red, color.green, color.blue, 255)
    }.toArray

    Image(360, 180, colorsA)
  }

}


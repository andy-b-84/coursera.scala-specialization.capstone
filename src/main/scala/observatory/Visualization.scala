package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import monix.reactive.Observable
import scala.concurrent.Await
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global

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
    val distancePower = 2
    val temperatureRectifier = 300 //"almost" Kelvin : cannot afford to have a 0 somewhere

    def greatCircleDistanceAngle(location1: Location, location2: Location): Angle = {
      val phi1 = Math.toRadians(location1.lat)
      val lambda1 = Math.toRadians(location1.lon)
      val phi2 = Math.toRadians(location2.lat)
      val lambda2 = Math.toRadians(location2.lon)
      2 * Math.asin(
        Math.sqrt(
          Math.pow(Math.sin(Math.abs(phi2 - phi1)/2), 2) +
          (
            Math.cos(phi1) *
            Math.cos(phi2) *
            Math.pow(Math.sin(Math.abs(lambda1 - lambda2)/2), 2)
          )
        )
      )
    }

    val temperaturesMap = temperatures.toMap
    if (temperaturesMap.isDefinedAt(location)) temperaturesMap(location)
    else {
      val set = temperaturesMap.map{ tuple => (tuple._1, (greatCircleDistanceAngle(location, tuple._1), tuple._2+temperatureRectifier)) }
      (set.aggregate(0:Temperature)((acc, tuple) => {
        val secondTuple = tuple._2
        acc + (secondTuple._2/Math.pow(secondTuple._1, distancePower))
      }, _+_) /
        set.aggregate(0:Temperature)((acc, tuple) => {acc + 1/Math.pow(tuple._2._1, distancePower)}, _+_)) - temperatureRectifier
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

      val n: Option[(Temperature, Color)] = None

      Await.result(Observable.fromIterable(points).foldLeftL((n, n)) { (acc, pair) =>
        if ((pair._1 > value) && (acc._1.isEmpty || (acc._1.get._1 - value > pair._1 - value))) {
          acc.copy(Some(pair), acc._2)
        } else if ((pair._1 < value) && (acc._2.isEmpty || (value - acc._2.get._1 > value - pair._1))) {
          acc.copy(acc._1, Some(pair))
        } else {
          acc
        }
      }.runAsync, 5.seconds) match {
        case (Some(min), None) => min._2
        case (None, Some(max)) => max._2
        case (Some(min), Some(max)) => interpolate(min, max)
        case _ => throw new Error("shouldn't get that")
      }
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val colorsA = Await.result(Observable.range(0,64500).map { arrayIndex =>
      val x = arrayIndex % 360
      val y = (arrayIndex - x) / 360

      val lon = x - 180
      val lat = 90 - y

      val temperature = predictTemperature(temperatures, Location(lat, lon))
      val color = interpolateColor(colors, temperature)

      val pixel = Pixel(color.red, color.green, color.blue, 255)

      arrayIndex -> pixel
    }.toListL.runAsync, 30.minutes).sortBy(_._1).toMap.values.toArray

    Image(360, 180, colorsA)
  }

}


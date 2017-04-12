package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import monix.reactive.Observable

import scala.concurrent.Await
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global
import scala.language.postfixOps

import scala.util.Try

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
    val distancePower = 10

    def greatCircleDistanceAngle(location1: Location, location2: Location): Angle = {
      val phi1 = Math.toRadians(location1.lat)
      val lambda1 = Math.toRadians(location1.lon)
      val phi2 = Math.toRadians(location2.lat)
      val lambda2 = Math.toRadians(location2.lon)

      val deltaLambda = Math.abs(lambda1 - lambda2)

      Math.atan2(
        Math.sqrt(
          Math.pow(Math.cos(phi2) * Math.sin(deltaLambda), 2) +
          Math.pow(
            (Math.cos(phi1) * Math.sin(phi2)) -
            (Math.sin(phi1) * Math.cos(phi2) * Math.cos(deltaLambda))
          , 2)
        ),
        (Math.sin(phi1) * Math.sin(phi2)) +
          (Math.cos(phi1) * Math.cos(phi2) * Math.cos(deltaLambda))
      )
    }

    val temperaturesMap = temperatures.toMap
    if (temperaturesMap.isDefinedAt(location)) temperaturesMap(location)
    else {
      val set = temperaturesMap.map{ tuple => (tuple._1, (greatCircleDistanceAngle(location, tuple._1), tuple._2)) }
      set.aggregate(0:Temperature)((acc, tuple) => {
        val secondTuple = tuple._2
        acc + (secondTuple._2/Math.pow(secondTuple._1, distancePower))
      }, _+_) /
        set.aggregate(0:Temperature)((acc, tuple) => {acc + 1/Math.pow(tuple._2._1, distancePower)}, _+_)
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

      val mini = points.foldLeft(n){(acc,point) =>
        if ((value - point._1 > 0) && (acc.isEmpty || (Math.abs(acc.get._1 - value)>Math.abs(point._1-value))))
          Some(point)
        else acc
      }
      val maxi = points.foldLeft(n){(acc,point) =>
        if ((value - point._1 < 0) && (acc.isEmpty || (Math.abs(acc.get._1 - value)>Math.abs(point._1-value))))
          Some(point)
        else acc
      }
      (mini, maxi) match {
        case (Some((_, c)), None) => c
        case (None, Some((_, c))) => c
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
    val cores = Try(Runtime.getRuntime.availableProcessors).toOption.getOrElse(1) match {
      case x if x > 1 => x
      case _ => 1
    }

    val step: Int = Math.ceil((360.0*180)/cores).toInt

    val obs = Observable.range(0, cores).mergeMap{ core =>
      val low = core * step
      val high = Math.min((core + 1) * step, 360*180)
      Observable.range(low, high).map{ arrayIndex =>
        val x = arrayIndex % 360
        val y = (arrayIndex - x) / 360

        val lon = x - 180
        val lat = 90 - y

        val temperature = predictTemperature(temperatures, Location(lat, lon))
        val color = interpolateColor(colors, temperature)

        val pixel = Pixel(color.red, color.green, color.blue, 255)

        arrayIndex -> pixel
      }
    }

    val colorsA = Await.result(obs.toListL.runAsync, 1 hour).sortBy(_._1).toMap.values.toArray

    Image(360, 180, colorsA)
  }

}


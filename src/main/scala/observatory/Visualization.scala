package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    val distancePower = 2

    def greatCircleDistanceAngle(location1: Location, location2: Location): Double = {
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
      val filteredSet = temperaturesMap.map{tuple =>
        greatCircleDistanceAngle(location, tuple._1) -> tuple._2
      }.filter{tuple =>
        Math.abs(tuple._1) < Math.PI/4
      }
      filteredSet.aggregate(0:Double)((acc, tuple) => acc + (tuple._2/Math.pow(tuple._1, distancePower)), _+_) /
        filteredSet.aggregate(0:Double)((acc, tuple) => acc + 1/tuple._1, _+_)
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    val scale = Map(
      60  -> Color(255, 255, 255),
      32  -> Color(255, 0,   0),
      12  -> Color(255, 255, 0),
      0   -> Color(0,   255, 255),
      -15 -> Color(0,   0,   255),
      -27 -> Color(255, 0,   255),
      -50 -> Color(33,  0,   107),
      -60 -> Color(0,   0,   0)
    )
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    ???
  }

}


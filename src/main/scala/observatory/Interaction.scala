package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Visualization.{interpolateColor, predictTemperature}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    val lon = ( ( x * 360.0 ) / Math.pow(2.0, zoom) ) - 180
    val latUnbound = Math.toDegrees(
      Math.atan(
        Math.sinh(
          Math.PI - ( ( 2.0 * Math.PI * y ) / Math.pow(2.0, zoom) )
        )
      )
    )
    val lat = if (latUnbound < -85.0511) -85.0511 else if (latUnbound > 85.0511) 85.0511 else latUnbound
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {
    val sortedColors = colors.toList.sortBy(_._1)

    val topLeftCorner = tileLocation(zoom, x, y)
   // println(s"[Interaction.tile] topLeftCorner = $topLeftCorner")
    //var maxLon = topLeftCorner.lon
    //var minLat = topLeftCorner.lat

    val zoomPower = Math.pow(2, zoom)

    val colorsA = Seq.range(0, 256*256).par.map{ arrayIndex =>
      val xTile = arrayIndex % 256
      val yTile = (arrayIndex - x) / 256

      val lon = topLeftCorner.lon + (xTile.toDouble/zoomPower)
      val lat = topLeftCorner.lat - (yTile.toDouble/zoomPower)

      //if (lon > maxLon) maxLon = lon
      //if (lat < minLat) minLat = lat

      val predictedTemperature = predictTemperature(temperatures, Location(lat, lon))

      val color = interpolateColor(sortedColors, predictedTemperature)
      //println(s"[Interaction.tile] (xTile, yTile) = ($xTile, $yTile),\t(lat, lon) = ($lat, $lon)," +
      // "\tpredictedTemperature = $predictedTemperature,\tcolor=$color")

      Pixel(color.red, color.green, color.blue, 127)
    }.toArray

    //println(s"[Interaction.tile] square : minLon = ${topLeftCorner.lon}, minLat = $minLat. maxLon = $maxLon, " +
    // "maxLat = ${topLeftCorner.lat}")

    Image(256, 256, colorsA)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit
  ): Unit = {
    ???
  }

}

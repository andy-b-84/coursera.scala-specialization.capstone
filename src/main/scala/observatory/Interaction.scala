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
    val lat = Math.toDegrees(
      Math.atan(
        Math.sinh(
          Math.PI - ( ( 2.0 * Math.PI * y ) / Math.pow(2.0, zoom) )
        )
      )
    )
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

    val xTileStart = (x*Math.pow(2, 9)).toInt
    val yTileStart = (y*Math.pow(2, 9)).toInt

    val colorsA = Seq.range(0, 256*256).par.map{ arrayIndex =>
      val xTile = arrayIndex % 256
      val yTile = (arrayIndex - x) / 256

      val location = tileLocation(zoom+9, xTileStart + (2*xTile) + 1, yTileStart + (2*yTile) + 1)

      val predictedTemperature = predictTemperature(temperatures, location)

      val color = interpolateColor(sortedColors, predictedTemperature)

      Pixel(color.red, color.green, color.blue, 127)
    }.toArray

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

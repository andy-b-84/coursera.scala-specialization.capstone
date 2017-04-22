package observatory

import com.sksamuel.scrimage.RGBColor

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int) {
  def pixel(alpha: Int = 255) = RGBColor(red, green, blue, alpha).toPixel
}

case class Tile(x: Double, y: Double, zoom: Int) {
  lazy val location: Location = Location(
    lat = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1.0 - 2.0 * y / (1 << zoom))))),
    lon = x / (1 << zoom) * 360.0 - 180.0
  )

  def toURI = new java.net.URI("http://tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png")
}
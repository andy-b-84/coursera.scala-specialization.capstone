package observatory

case class Location(lat: Double, lon: Double) {
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case l:Location => (this.lat.toInt == l.lat.toInt) && (this.lon.toInt == l.lon.toInt)
      case _ => super.equals(obj)
    }
  }
}

case class Color(red: Int, green: Int, blue: Int)


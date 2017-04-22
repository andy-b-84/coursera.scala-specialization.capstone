package observatory

import Extraction._
import Visualization._
import Interaction._

object Main extends App {
  /*val year = 1976
  printlnTime("start")
  val averages = locationYearlyAverageRecords(locateTemperatures(year, "/stations.csv", s"/$year.csv"))
  printlnTime("averages calculated")
  //val image = visualize(averages, scale)
  //printlnTime("image filled")
  //image.output(new java.io.File(s"target/$year.png"))
  //printlnTime("image written")

  val imageMercator = tile(averages, scale, 0, 0, 0)
  printlnTime("image filled")
  imageMercator.output(new java.io.File(s"target/$year.mercator.png"))
  printlnTime("image written")*/

  def generateImage(year: Int, zoom: Int, x: Int, y: Int, temperatures: Temperatures): Unit = {
    new java.io.File(s"target/temperatures/$year/$zoom").mkdirs()
    val image = tile(temperatures, scale, zoom, x, y)
    printlnTime(s"year $year zoom $zoom x $x y $y : image filled")
    image.output(new java.io.File(s"target/temperatures/$year/$zoom/$x-$y.png"))
    printlnTime(s"year $year zoom $zoom x $x y $y : image written")
  }

  Seq.range(2015, 2016).foreach { year =>
    printlnTime(s"starting year $year")
    val averages = locationYearlyAverageRecords(locateTemperatures(year, "/stations.csv", s"/$year.csv"))
    printlnTime(s"averages calculated")
    generateTiles(Map(year -> averages), generateImage)
  }
}

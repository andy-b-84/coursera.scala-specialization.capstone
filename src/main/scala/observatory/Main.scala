package observatory

import Extraction._
import Visualization._
import Interaction._

object Main extends App {
  val year = 1976
  printlnTime("start")
  val averages = locationYearlyAverageRecords(locateTemperatures(year, "/stations.csv", s"/$year.csv"))
  printlnTime("averages calculated")
  val image = visualize(averages, scale)
  printlnTime("image filled")
  image.output(new java.io.File(s"target/$year.png"))
  printlnTime("image written")

  val imageMercator = tile(averages, scale, 0, 0, 0)
  imageMercator.output(new java.io.File(s"target/$year.mercator.png"))
}

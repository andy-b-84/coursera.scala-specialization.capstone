package observatory

import Extraction._
import Visualization._

object Main extends App {
  val year = 1976
  printlnTime("start")
  val averages = locationYearlyAverageRecords(locateTemperatures(year, "/stations.csv", s"/$year.csv"))
  printlnTime("averages calculated")
  val image = visualize(averages, scale)
  printlnTime("image filled")
  image.output(new java.io.File(s"target/$year.png"))
  printlnTime("image written")
}

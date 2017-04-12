package observatory

import Extraction._
import Visualization._

object Main extends App {
  override def main(args: Array[String]): Unit = {
    super.main(args)
    printlnTime("start")
    val averages = locationYearlyAverageRecords(locateTemperatures(1975, "/stations.csv", "/1975.csv"))
    printlnTime("averages calculated")
    val image = visualize(averages, Map(
      60.0  -> Color(255,255,255),
      32.0  -> Color(255,0,  0),
      12.0  -> Color(255,255,0),
      0.0   -> Color(0,  255,255),
      -15.0 -> Color(0,  0,  255),
      -27.0 -> Color(255,0,  255),
      -50.0 -> Color(33, 0,  107),
      -60.0 -> Color(0,  0,  0)
    ))
    printlnTime("image filled")
    image.output(new java.io.File("target/1975.png"))
    printlnTime("image written")
  }
}

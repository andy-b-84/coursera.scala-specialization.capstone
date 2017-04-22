import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by andy_b_84 on 05/04/2017.
 */
package object observatory {
  type ObsYear = Int
  type ObsMonth = Int
  type ObsDay = Int

  type Channel = Int

  type Latitude = Double
  type Longitude = Double

  type Temperature = Double
  type Angle = Double

  type Temperatures = Iterable[(Location, Temperature)]

  type Size = Long

  type Stn = String
  type Wban = String

  private val sdf = new SimpleDateFormat("HH:mm:ss")

  def printlnTime(msg: String, context: String = "main"): Unit = {
    val now = Calendar.getInstance()
    println(s"[$context] "+sdf.format(now.getTime)+" "+msg)
  }

  val scale = Map(
    60.0  -> Color(255,255,255),
    32.0  -> Color(255,0,  0),
    12.0  -> Color(255,255,0),
    0.0   -> Color(0,  255,255),
    -15.0 -> Color(0,  0,  255),
    -27.0 -> Color(255,0,  255),
    -50.0 -> Color(33, 0,  107),
    -60.0 -> Color(0,  0,  0)
  )
}

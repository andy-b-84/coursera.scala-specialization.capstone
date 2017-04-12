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

  type Size = Long

  type Stn = String
  type Wban = String

  private val sdf = new SimpleDateFormat("HH:mm:ss")

  def printlnTime(msg: String, context: String = "main"): Unit = {
    val now = Calendar.getInstance()
    println(s"[$context] "+sdf.format(now.getTime)+" "+msg)
  }
}

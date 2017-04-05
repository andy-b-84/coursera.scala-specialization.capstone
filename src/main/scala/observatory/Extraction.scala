package observatory

import java.io._
import java.time.{LocalDate, Month}

import monix.reactive.{Consumer, Observable}
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
  * 1st milestone: data extraction
  */
object Extraction {
  private def filenameToObservableAndHanlde(filename: String): (Observable[String], InputStream) = {
    val inputStream = Extraction.getClass.getResourceAsStream(filename)
    (Observable.fromLinesReader(new BufferedReader(new InputStreamReader(inputStream, "ASCII"))), inputStream)
  }

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: ObsYear, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    val (temperaturesObservable, temperaturesFileHandle) = filenameToObservableAndHanlde(temperaturesFile)

    def toDoubleDefault(s: String, default: Double): Double = {
      Try(s.toDouble).toOption.getOrElse(default)
    }

    val (stationsObservable, stationsFileHandle) = filenameToObservableAndHanlde(stationsFile)

    val stationsMap: Map[(Stn, Wban), (Latitude, Longitude)] = Await.result(stationsObservable.filter{ line =>
      line.takeRight(2) != ",,"
    }.map{ line =>
      val tmp = line.split(",", 4)
      ((tmp(0), tmp(1)),(toDoubleDefault(tmp(2), 0), toDoubleDefault(tmp(3), 0)))
    }.toListL.runAsync, 1.minute).toMap

    val r = Await.result(temperaturesObservable.map { temperaturesLine =>
      val tmpA = temperaturesLine.split(",")
      (tmpA(0), tmpA(1), tmpA(2).toInt, tmpA(3).toInt, tmpA(4).toDouble)
    }.filter { tuple => stationsMap.isDefinedAt((tuple._1, tuple._2))
    }.map{ tuple =>
      val (stn, wban, month, day, temperature) = tuple
      val tmpB = stationsMap((stn, wban))
      val (lat, lon) = (tmpB._1, tmpB._2)
      (LocalDate.of(year, Month.of(month), day), Location(lat, lon), temperature)
    }.toListL.runAsync, 5.minutes)

    stationsFileHandle.close()
    temperaturesFileHandle.close()

    r
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    ???
  }

}

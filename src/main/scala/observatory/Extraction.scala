package observatory

import java.io._
import java.time.{LocalDate, Month}

import monix.reactive.Observable
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
  * 1st milestone: data extraction
  */
object Extraction {
  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: ObsYear, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    def filenameToObservableAndHandle(filename: String): (Observable[String], InputStream) = {
      val inputStream = Extraction.getClass.getResourceAsStream(filename)
      (Observable.fromLinesReader(new BufferedReader(new InputStreamReader(inputStream, "ASCII"))), inputStream)
    }

    def toDoubleDefault(s: String, default: Double): Double = {
      Try(s.toDouble).toOption.getOrElse(default)
    }

    def fahrenheitToCelsius(fahrenheit: Temperature): Temperature = (fahrenheit-32)/1.8

    val (temperaturesObservable, temperaturesFileHandle) = filenameToObservableAndHandle(temperaturesFile)

    val (stationsObservable, stationsFileHandle) = filenameToObservableAndHandle(stationsFile)

    val stationsMap: Map[(Stn, Wban), (Latitude, Longitude)] = Await.result(stationsObservable.filter{ line =>
      line.takeRight(2) != ",,"
    }.map{ line =>
      val tmp = line.split(",", 4).toSeq
      val result = ((tmp.head, tmp(1)),(toDoubleDefault(tmp(2), 0), toDoubleDefault(tmp(3), 0)))
      //println(s"line = $line , tmp = ${tmp.toString} , result = $result")
      result
    }.toListL.runAsync, 1.minute).toMap
    //stationsMap.foreach(println)
    //throw new Error

    val r = Await.result(temperaturesObservable.map { temperaturesLine =>
      val tmpA = temperaturesLine.split(",")
      (tmpA(0), tmpA(1), tmpA(2).toInt, tmpA(3).toInt, tmpA(4).toDouble)
    }.filter { tuple => stationsMap.isDefinedAt((tuple._1, tuple._2)) && (tuple._5 < 999)
    }.map{ tuple =>
      val (stn, wban, month, day, temperature) = tuple
      val tmpB = stationsMap((stn, wban))
      val (lat, lon) = (tmpB._1, tmpB._2)
      (LocalDate.of(year, Month.of(month), day), Location(lat, lon), fahrenheitToCelsius(temperature))
    }.toListL.runAsync, 5.minutes)

    stationsFileHandle.close()
    temperaturesFileHandle.close()

    r
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    Await.result(Observable.fromIterable(records).foldLeftL(Map[Location, (Temperature, Size)]()){ (acc, currentTuple) =>
      if (acc.isDefinedAt(currentTuple._2)) {
        val newTemperature = currentTuple._3 + acc(currentTuple._2)._1
        val newSize = acc(currentTuple._2)._2 + 1
        acc.updated(currentTuple._2, (newTemperature, newSize))
      } else {
        val newTuple = (currentTuple._2, (currentTuple._3, 1.toLong))
        acc + newTuple
      }
    }.map{taskMap => taskMap.map { tuple =>
      val loc = tuple._1
      val temperatureTuple = tuple._2
      (Location(loc.lat, loc.lon), temperatureTuple._1 / temperatureTuple._2)
    }}.runAsync, 5.minutes)
  }

}

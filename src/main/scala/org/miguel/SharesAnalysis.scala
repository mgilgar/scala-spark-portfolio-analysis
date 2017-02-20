package org.miguel

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object SharesAnalysis {
	type Ticket = String

	def basePrice(ticket: Ticket): Double = {
		ticket match {
			case "AAPL" => 100.0
			case "TSLA" => 200.0
			case "AMZN" => 600.0
			case _ => 300.0
		}
	}

	def nextSharePrice(ticket: Ticket, price: Option[Double]) = {
        val variations = Vector(1.0, 0.5, 1.1, 0.3, 0.2, 0.5, 0.7, -0.1, -1.5, -3.0, -1.0, -0.1, 5.0, -7.5, 3.0, 2.23)
        val position = scala.util.Random.nextInt(variations.length)
        val variation = variations(position)
        price.getOrElse(basePrice(ticket))*(1 + variation)
	}

	def nextTicket() = {
		val tickets = Vector("AAPL", "TSLA", "AMZN")
		val position = scala.util.Random.nextInt(tickets.length)
		tickets(position)
	}

	def main(args: Array[String]) = {
		val conf = new SparkConf().setAppName("Miguel Shares Analysis")
		val sc = new SparkContext(conf)

		case class SharePrice(instances: Int, price: Double)

		val sharePrices = (1 to 1000000).map {_ => {
			val ticket = nextTicket()
			val sharePrice = nextSharePrice(ticket, None)
			(ticket, SharePrice(1, sharePrice))
		}}

		val sharePricessRdd = sc.parallelize(sharePrices)

		val sharePricessRddReducedByKey = sharePricessRdd.reduceByKey((x,y) => SharePrice(x.instances + y.instances, x.price + y.price))

		val shareAndAveragePrice = sharePricessRddReducedByKey.map { shareData =>
			(shareData._1, shareData._2.price/shareData._2.instances)
		}

		shareAndAveragePrice.foreach(println)

		val count = sharePricessRddReducedByKey.count()
		println(s"Total number of shares $count")
	}
}

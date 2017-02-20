package org.miguel

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd._

object PortfolioAnalysis {
	type Ticket = String
	type Operation = String

	def nextTicket() = {
		val tickets = Vector("AAPL", "TSLA", "AMZN", "FB", "EBAY")
		val position = scala.util.Random.nextInt(tickets.length)
		tickets(position)
	}

	def currentPrice(ticket: Ticket) = {
		ticket match {
			case "AAPL" => 116.74
			case "TSLA" => 219.74
			case "AMZN" => 772.13
			case "FB" => 116.92
			case "EBAY" => 30.1
			case _ => 300.0
		}
	}

	def nextSharePrice(ticket: Ticket, price: Option[Double]): Double = {
        val variations = Vector(1.0, 0.5, 1.1, 0.3, 0.2, 0.5, 0.7, -0.1, -1.5, -3.0, -1.0, -0.1, 5.0, -7.5, 3.0, 2.23)
        val position = scala.util.Random.nextInt(variations.length)
        val variation = variations(position)
        price.getOrElse(currentPrice(ticket))*(1 + variation)
	}

	def nextQuantity(): Int = {
		scala.util.Random.nextInt(100)
	}

	def nextOperation(): Operation = {
		if (scala.util.Random.nextBoolean) {
			"BUY"
		} else {
			"SELL"
		}
	}

	def main(args: Array[String]) = {
		val conf = new SparkConf().setAppName("Miguel Portfolio Analysis")
		val sc = new SparkContext(conf)

		val sharePrices = (1 to 1000).map {_ => {
			val ticket = nextTicket()
			val operation = nextOperation()
			val quantity  = nextQuantity()
			val sharePrice = nextSharePrice(ticket, None)
			(ticket, operation, quantity, sharePrice)
		}}

		val sharePricesRdd = sc.parallelize(sharePrices)

		// Total cashFlow
		{ 
			val sharePricesWithoutTicketRdd = sharePricesRdd.map { shareData => (shareData._2, (shareData._3, shareData._4))} 
			val sharePricesWithoutTicketWithTotalAmount = sharePricesWithoutTicketRdd.map { shareData => (shareData._1, shareData._2._1*shareData._2._2)}
			val sharePricesWithoutTicketReduceByOperation = sharePricesWithoutTicketWithTotalAmount.reduceByKey((x, y) => x+y)
			sharePricesWithoutTicketReduceByOperation.foreach(println)
		}
		// Per ticket and per operation, average price, total transfer, quantity
		{	
			case class ShareKey(ticket: Ticket, operation: Operation)
			case class ShareUnitPrice(quantity: Int, unitPrice: Double)
			case class SharePrice(quantity: Int, price: Double)
			implicit val sortShareKey = new Ordering[ShareKey] {
				override def compare(sp1: ShareKey, sp2: ShareKey) = {
					val ticketComparison = sp1.ticket.toString.compare(sp2.ticket.toString)
					if (ticketComparison==0) {
						sp1.operation.toString.compare(sp2.operation.toString)
					} else {
						ticketComparison
					}
				}
			}
			val sharePricesRemapRdd = sharePricesRdd.map { shareData =>
			 (ShareKey(shareData._1, shareData._2), ShareUnitPrice(shareData._3, shareData._4))
			}
			
			val sharesReport: RDD[(ShareKey, SharePrice)] = sharePricesRemapRdd.combineByKey(
				value => SharePrice(value.quantity, value.unitPrice*value.quantity),
				(acc: SharePrice, value) => SharePrice(acc.quantity + value.quantity, acc.price + value.unitPrice*value.quantity),
				(acc1: SharePrice, acc2: SharePrice) => SharePrice(acc1.quantity + acc2.quantity, acc1.price + acc2.price)
				)
			// No funciona el sort
			val sharesReportSorted = sharesReport.sortByKey()
			sharesReportSorted.foreach(println)
		}

		println("The End")

//		sharePricesWithoutTicketGroupByOperation.fold() (shareData => (shareData._1, shareData._2*shareData._3))

		//.reduce((xx, yy) => xx._1*xx._2 + yy._1*yy._2)
/*
		val sharePricesGroupedByTicket = sharePricesRdd.map { shareData =>
			((shareData._1, shareData._2), (shareData._3, shareData._4))
		}.groupByKey()*/

/*

		val sharePricesGroupedByOperation = sharePricesGroupedByTicket._2.groupByKey()

		sharePricesGroupedByOperation.map { shareData =>
			(sharePricesGroupedByTicket._1, shareData._1, shareData._2)}
*/
		//val count = sharePricesGroupedByTicket.count()
		//println(s"count $count")

/*
		val sharePricessRddReducedByKey = sharePricesRdd.reduceByKey((x,y) => SharePrice(x.instances + y.instances, x.price + y.price))

		val shareAndAveragePrice = sharePricessRddReducedByKey.map { shareData =>
			(shareData._1, shareData._2.price/shareData._2.instances)
		}

		shareAndAveragePrice.foreach(println)

		val count = sharePricessRddReducedByKey.count()
		println(s"Total number of shares $count")
*/
//		sharePricessRdd.

/*
        val r = scala.util.Random
        r.nextFloat(250)

        val inputFile = args(0)
      	val outputFile = args(1)	
		val conf = new SparkConf().setAppName("Miguel Test")
		val sc = new SparkContext(conf)
		val input = sc.textFile(inputFile)
		val words = input.flatMap(line => line.split(" "))
		val counts = words.map(word => (word, 1)).reduceByKey{ case (x, y) => x + y}
		counts.saveAsTextFile(outputFile)*/
	}
}

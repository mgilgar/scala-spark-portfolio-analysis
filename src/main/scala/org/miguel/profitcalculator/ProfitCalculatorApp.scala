package org.miguel.profitcalculator

import org.joda.time.DateTime
import org.miguel.profitcalculator.models.{ Buy, Deposit, Price, Sell }

/**
  * Created by miguelgil_garcia on 24/02/2017.
  */
object ProfitCalculatorApp extends App {
//  val conf = new SparkConf().setAppName("Miguel Profit Calculator")
//  val sc = new SparkContext(conf)
  val profitCalculator = StandardProfitCalculator
  val profitReportTablePrinter = ProfitReportTablePrinter

  val operations = List(
    Price(DateTime.parse("2017-01-01"), "AAPL", 10000),
    Deposit(DateTime.parse("2017-01-01"), 10000000),
    Buy(DateTime.parse("2017-01-01"), "AAPL", 10000, 100),
    Price(DateTime.parse("2017-01-02"), "AAPL", 10220),
    Buy(DateTime.parse("2017-01-02"), "AAPL", 10200, 20),
    Price(DateTime.parse("2017-01-03"), "AAPL", 10700),
    Sell(DateTime.parse("2017-01-3"), "AAPL", 10700, 50),
    Price(DateTime.parse("2017-01-04"), "AAPL", 11000),
    Buy(DateTime.parse("2017-01-04"), "AAPL", 11000, 50),
    Price(DateTime.parse("2017-01-05"), "AAPL", 11220),
    Price(DateTime.parse("2017-01-06"), "AAPL", 11720),
    Price(DateTime.parse("2017-01-07"), "AAPL", 11020),
    Price(DateTime.parse("2017-01-08"), "AAPL", 10820),
    Price(DateTime.parse("2017-01-09"), "AAPL", 11720),
    Buy(DateTime.parse("2017-01-10"), "AAPL", 12500, 200),
    Sell(DateTime.parse("2017-01-10"), "AAPL", 12800, 40),
    Sell(DateTime.parse("2017-01-10"), "AAPL", 13000, 20),
    Sell(DateTime.parse("2017-01-10"), "AAPL", 11500, 260),
    Price(DateTime.parse("2017-01-11"), "AAPL", 11720),
    Price(DateTime.parse("2017-01-12"), "AAPL", 10020),
    Price(DateTime.parse("2017-01-13"), "AAPL", 9820),
    Price(DateTime.parse("2017-01-14"), "AAPL", 10320)
  )

  //val operationsRdd = sc.parallelize(operations)

  def operationsUntilDate(date: DateTime) = operations.filter (_.getDate.isBefore(date))
  //def operationsRddUntilDate(date: DateTime) = operationsRdd.filter (_.getDate.isBefore(date))

  val today = DateTime.now()
  println(s"\n\ncalculate till now: ${profitCalculator.calculateTotalProfitLine(operationsUntilDate(today), Some(today), Some(10710))}")
  val oneMonthAgo = DateTime.now().minusMonths(1)
  println(s"\n\ncalculate till now - 1 month: ${profitCalculator.calculateTotalProfitLine(operationsUntilDate(oneMonthAgo), Some(oneMonthAgo), Some(10870))}")
  val fortyThreeDaysAgo = DateTime.now().minusDays(43)
  println(s"\n\ncalculate till now - 43 days: ${profitCalculator.calculateTotalProfitLine(operationsUntilDate(fortyThreeDaysAgo), Some(fortyThreeDaysAgo), Some(10570))}")
  val fortyFourDaysAgo = DateTime.now().minusDays(44)
  println(s"\n\ncalculate till now - 44 days: ${profitCalculator.calculateTotalProfitLine(operationsUntilDate(fortyFourDaysAgo), Some(fortyFourDaysAgo), Some(9070))}")

  val profitTable = profitCalculator.calculateTotalProfitTable(operations)

  profitReportTablePrinter.print(profitTable)

}

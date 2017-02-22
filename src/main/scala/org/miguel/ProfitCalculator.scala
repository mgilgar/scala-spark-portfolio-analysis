package org.miguel

import org.joda.time.{DateTime, Days}

object ProfitCalculator extends App {
  val operations = List(
    Deposit(DateTime.parse("2017-01-01"), 100000),
    Buy(DateTime.parse("2017-01-01"), "AAPL", 100.0, 100),
    Buy(DateTime.parse("2017-01-02"), "AAPL", 102.0, 20),
    Sell(DateTime.parse("2017-01-3"), "AAPL", 107.0, 50),
    Buy(DateTime.parse("2017-01-04"), "AAPL", 110.0, 50),
    Buy(DateTime.parse("2017-01-10"), "AAPL", 125.0, 200),
    Sell(DateTime.parse("2017-01-10"), "AAPL", 128.0, 40),
    Sell(DateTime.parse("2017-01-10"), "AAPL", 130.0, 20),
    Sell(DateTime.parse("2017-01-10"), "AAPL", 115.0, 260)
  )

  case class ProfitReport(total: Double, relative: Double, tae: Double,
                          remainingShares: Int, currentPrice: Double, valueShares: Double,
                          cash: Double, value: Double) {
    override def toString() = {
      s"\n===================================" +
      s"\nTotal profit: ${total}" +
      s"\nRelative profit (Total Profit/ Cash Available): $relative" +
      s"\ntae (relative profit anualised): $tae" +
      s"\n\nShares in the portfolio: $remainingShares" +
      s"\nCurrent stock price: $currentPrice" +
        s"\nCurrent value of shares in the portfolio: $valueShares" +
      s"\n\nCash not invested: $cash" +
      s"\nPortfolio current value: $value" +
        s"\n==================================="
    }
  }

  def calculateTotalProfit(operations: List[Operation], today: DateTime, currentPrice: Double): ProfitReport = {
    val firstOperationDate = operations.head match {
      case Buy(date, ticket, price, quantity) => date
      case Sell(date, ticket, price, quantity) => date
      case Deposit(date, amount) => date
      case _ => DateTime.now()
    }

    val numberOfDays = Days.daysBetween(firstOperationDate, today)

    val buys: List[Buy] = operations.collect { case op: Buy => op }
    val totalBuys = buys.foldLeft(0.0)((acc, item) => acc + item.price * item.quantity)
    val totalBuysShares: Int = buys.foldLeft(0)((acc, item) => acc + item.quantity).toInt
//    println(s"totalBuys:$totalBuys")
    val sells = operations.collect { case op: Sell => op }
    val totalSells = sells.foldLeft(0.0)((acc, item) => acc + item.price * item.quantity)
    val totalSellsShares: Int = sells.foldLeft(0)((acc, item) => acc + item.quantity).toInt
//    println(s"totalSells:$totalSells")
    val deposits = operations.collect { case op: Deposit => op }
    val totalDeposits = deposits.foldLeft(0.0)((acc, item) => acc + item.amount)
//    println(s"totalDeposits:$totalDeposits")
    val remainingShares = totalBuysShares - totalSellsShares
    val totalProfit = totalSells - totalBuys + remainingShares * currentPrice
    val relativeProfit = 100*(totalProfit / totalDeposits)
    val tae = (totalProfit / totalDeposits) * (365/numberOfDays.getDays)*100

    val cash = totalDeposits + totalSells - totalBuys

    val value = totalDeposits + totalProfit

    ProfitReport(totalProfit, relativeProfit, tae, remainingShares, currentPrice,remainingShares*currentPrice, cash, value)
  }

  def operationsUntilDate(date: DateTime) = operations.filter (_.getDate.isBefore(date))
  val today = DateTime.now()
  println(s"\n\ncalculate till now: ${calculateTotalProfit(operationsUntilDate(today), today, 107.10)}")
  val oneMonthAgo = DateTime.now().minusMonths(1)
  println(s"\n\ncalculate till now - 1 month: ${calculateTotalProfit(operationsUntilDate(oneMonthAgo), oneMonthAgo, 108.70)}")
  val fortyThreeDaysAgo = DateTime.now().minusDays(43)
  println(s"\n\ncalculate till now - 43 days: ${calculateTotalProfit(operationsUntilDate(fortyThreeDaysAgo), fortyThreeDaysAgo, 105.70)}")
  val fortyFourDaysAgo = DateTime.now().minusDays(44)
  println(s"\n\ncalculate till now - 44 days: ${calculateTotalProfit(operationsUntilDate(fortyFourDaysAgo), fortyFourDaysAgo, 90.70)}")

}

sealed trait Operation {
  def getDate: DateTime = {
    this match {
      case Buy(date, ticket, price, quantity) => date
      case Sell(date, ticket, price, quantity) => date
      case Deposit(date, amount) => date
      case _ => DateTime.now // TODO
    }
  }
}

case class Buy(date: DateTime, ticket: String, price: Double, quantity: Int) extends Operation
case class Sell(date: DateTime, ticket: String, price: Double, quantity: Int) extends Operation
case class Deposit(date: DateTime, amount: Int) extends Operation
case class Withdaraw(date: DateTime, amount: Int) extends Operation
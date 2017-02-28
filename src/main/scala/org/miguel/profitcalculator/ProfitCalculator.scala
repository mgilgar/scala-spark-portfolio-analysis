package org.miguel.profitcalculator

import org.joda.time.{ DateTime, Days }
import org.miguel.profitcalculator.models._

class StandardProfitCalculator {
  def firstOperationDate(operations: List[Operation]) = operations.head.getDate
  def lastOperationDate(operations: List[Operation]) = operations.reverse.head.getDate

  // PRE: In operations there should be a Price for every date.
  def calculateTotalProfitLine(operations: List[Operation], currentDate: Option[DateTime] = None, currentPrice: Option[Int] = None): ProfitReportItem = {

    // TODO sort properly
    val operationsSorted = operations
    val operationsFiltered = currentDate.map { date => operations.filter(_.getDate.isBefore(currentDate.getOrElse(DateTime.now).plusMillis(1)))}.getOrElse(operations)

    val operationsGrouped = operationsFiltered.groupBy(_.getClass)
    val prices = operationsGrouped.get(classOf[Price]).getOrElse(List.empty).asInstanceOf[List[Price]]
    val buys = operationsGrouped.get(classOf[Buy]).getOrElse(List.empty).asInstanceOf[List[Buy]]
    val sells = operationsGrouped.get(classOf[Sell]).getOrElse(List.empty).asInstanceOf[List[Sell]]
    val deposits = operationsGrouped.get(classOf[Deposit]).getOrElse(List.empty).asInstanceOf[List[Deposit]]

    lazy val pricesReverse: List[Price] = prices.reverse
    lazy val lastRecordedPrice = pricesReverse.head.price
    lazy val lastRecordedDate = pricesReverse.head.date
    val lastPrice = currentPrice.getOrElse(lastRecordedPrice)
    val lastDate = currentDate.getOrElse(lastRecordedDate)

    // TODO: That means it will calculate the profit till the data of last operation, not today
    val numberOfDays = Days.daysBetween(firstOperationDate(operations), lastDate)

    val totalBuys = buys.foldLeft(0)((acc, item) => acc + item.price * item.quantity)
    val totalBuysShares: Int = buys.foldLeft(0)((acc, item) => acc + item.quantity).toInt
    val totalSells = sells.foldLeft(0)((acc, item) => acc + item.price * item.quantity)
    val totalSellsShares: Int = sells.foldLeft(0)((acc, item) => acc + item.quantity).toInt
    val totalDeposits = deposits.foldLeft(0)((acc, item) => acc + item.amount)

    // Reports
    val remainingShares = totalBuysShares - totalSellsShares
    val totalProfit = totalSells - totalBuys + remainingShares * lastPrice
    val relativeProfit = (if (totalDeposits>0) { 100*(totalProfit / totalDeposits) } else { -1 }).toInt
    val tae = (if (totalDeposits>0 && numberOfDays.getDays>0) {(totalProfit / totalDeposits) * (365/numberOfDays.getDays)*100} else { -1 }).toInt
    val cash = totalDeposits + totalSells - totalBuys
    val value = totalDeposits + totalProfit

    ProfitReportItem(lastDate, totalProfit, relativeProfit, tae, remainingShares, lastPrice, remainingShares*lastPrice, cash, value)
  }

  def calculateTotalProfitTable(operations: List[Operation]): List[ProfitReportItem] = {
    val numberOfDays = Days.daysBetween(operations.head.getDate, operations.reverse.head.getDate).getDays
    (0 to numberOfDays).map { dayOrder =>
      val currentDate = operations.head.getDate.plusDays(dayOrder)
      val currentProfit = calculateTotalProfitLine(operations, Some(currentDate), None)
      currentProfit
    }.toList
  }
}
/*
class SparkProfitCalculator {
  def calculateTotalProfit(operations: RDD[Operation], today: DateTime, currentPrice: Double): ProfitReport = {
    val firstOperationDate = operations.first match {
      case Buy(date, ticket, price, quantity) => date
      case Sell(date, ticket, price, quantity) => date
      case Deposit(date, amount) => date
      case _ => DateTime.now()
    }

    val numberOfDays = Days.daysBetween(firstOperationDate, today)

   // val buys: List[Buy] = operations.collect { case op: Buy => op }
   val buys: List[Buy] = operations.filter { op => op.getClass() == classOf[Buy] }
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
}
*/

object StandardProfitCalculator extends StandardProfitCalculator





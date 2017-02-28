package org.miguel.profitcalculator

import org.joda.time.format.DateTimeFormat
import org.miguel.profitcalculator.models.ProfitReportItem

/**
  * Created by miguelgil_garcia on 24/02/2017.
  */
class ProfitReportTablePrinter {
  val DateHeader = "Date         "
  val TotalProfitHeader = "Total Profit"
  val RelativeProfitHeader = "Relative Profit"
  val TAEHeader = "TAE    "
  val SharesInPortfolioHeaderHeader = "Shares in portfolio"
  val CurrentSharePriceHeader = "Current share price"
  val CurrentValueOfPortfolioHeader = "Current value of Portfolio"
  val CashAvailableHeader = "Cash Available"
  val TotalValueHeader = "Total Value"
  val Headers = List(DateHeader, TotalProfitHeader, RelativeProfitHeader, TAEHeader, SharesInPortfolioHeaderHeader, CurrentSharePriceHeader,
    CurrentValueOfPortfolioHeader,CashAvailableHeader,TotalValueHeader)
  val HeaderString = s"| ${Headers.mkString(" | ")} |"
  val separationString = s"+${"".padTo(HeaderString.length-2, "-").mkString}+"

  val YYYYMMDDDateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-DD")



  def print(profitReportItems: List[ProfitReportItem]) = {
    println(separationString)
    println(HeaderString)
    println(separationString)
    profitReportItems.map(profitReportItem => println(getLine(profitReportItem)))
    println(separationString)
  }

  def getLine(profitReport: ProfitReportItem) = {
    val mainContentList = List(
      (profitReport.total, TotalProfitHeader, true),
      (profitReport.relative, RelativeProfitHeader, true),
      (profitReport.tae, TAEHeader, true),
      (profitReport.remainingShares, SharesInPortfolioHeaderHeader, false),
      (profitReport.currentPrice, CurrentSharePriceHeader, true),
      (profitReport.valueShares, CurrentValueOfPortfolioHeader, true),
      (profitReport.cash, CashAvailableHeader, true),
      (profitReport.value, TotalValueHeader, true))
    val mainContentListProcessed = mainContentList.map{ case (content, header, valueIsCents) => getCell(content, header, valueIsCents)}
    val mainContent = mainContentListProcessed.mkString(" | ")

    val dateFormatted = YYYYMMDDDateTimeFormatter.print(profitReport.date)
    val dateContent = pad(s"${dateFormatted}", DateHeader)
    "| " + dateContent + " | " + mainContent + " |"
  }

  private def getCell(value: Int, header: String, valueIsCents: Boolean = true) = {
    def toCurrency(value: Int, valueIsCents: Boolean) = {
      if (valueIsCents) {
        f"${value.toDouble / 100}%1.2f"
      } else {
        f"$value"
      }
    }
    pad(toCurrency(value, valueIsCents).toString, header)
  }

  private def pad(value: String, header: String) = {
    value.reverse.padTo(header.length, " ").reverse.mkString
  }

}

object ProfitReportTablePrinter extends ProfitReportTablePrinter

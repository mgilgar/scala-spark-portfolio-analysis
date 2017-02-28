package org.miguel.profitcalculator.models

import org.joda.time.DateTime

case class ProfitReportItem(date: DateTime, total: Int, relative: Int, tae: Int,
                            remainingShares: Int, currentPrice: Int, valueShares: Int,
                            cash: Int, value: Int) {
                          override def toString() = {
                            s"\n===================================" +
                              s"\nDate: ${date}" +
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

                          /*def toStringLine() = {
                            s2.padTo(padLength, " ")
                            s"${total.padTo()}, $relative, $tae, $remainingShares, $currentPrice, $valueShares, $cash, $value"
                          }*/
                        }

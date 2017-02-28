package org.miguel.profitcalculator.models

import org.joda.time.DateTime

sealed trait Operation {
  def getDate: DateTime = {
    this match {
      case Price(date, ticket, price) => date
      case Buy(date, ticket, price, quantity) => date
      case Sell(date, ticket, price, quantity) => date
      case Deposit(date, amount) => date
      case _ => DateTime.now // TODO
    }
  }
}

case class Price(date: DateTime, ticket: String, price: Int) extends Operation
case class Buy(date: DateTime, ticket: String, price: Int, quantity: Int) extends Operation
case class Sell(date: DateTime, ticket: String, price: Int, quantity: Int) extends Operation
case class Deposit(date: DateTime, amount: Int) extends Operation
case class Withdraw(date: DateTime, amount: Int) extends Operation





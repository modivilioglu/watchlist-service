package com.mod.watchlist

import cats.Monoid
import cats.implicits._
package object service {
  case class Content(id: String)

  // Watchlist has contents and belongs to a Customer
  case class CustomerWatchList(list: List[Content], customer: Customer)

  implicit class CustomerWatchListOperations(customerWatchList: CustomerWatchList) {

    def |-| (contents: List[Content]) : CustomerWatchList = {
      val newList = customerWatchList.list.filter(content => !contents.contains(content))
      CustomerWatchList(newList, customerWatchList.customer)
    }

    def |+| (contents: List[Content]) : CustomerWatchList = {
      val newList = Monoid[List[Content]].combine(customerWatchList.list, contents).distinct
      CustomerWatchList(newList, customerWatchList.customer)
    }
  }

  implicit def fromStringToContent(id: String): Content = Content(id)
  case class Customer(id: String)
}

package com.mod.watchlist.dao

import com.mod.watchlist.service.{Content, Customer, CustomerWatchList}

trait WatchListDao {
  def getWatchList(customer: Customer): CustomerWatchList
  def addToWatchList(contents: List[Content], customer: Customer): CustomerWatchList
  def deleteFromWatchList(contents: List[Content], customer: Customer): CustomerWatchList
}

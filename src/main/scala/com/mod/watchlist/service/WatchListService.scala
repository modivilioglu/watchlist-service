package com.mod.watchlist.service

trait WatchListService {
  def addContentToWatchList(contents: List[Content], customer: Customer): CustomerWatchList
  def deleteContentFromWatchList(contents: List[Content], customer: Customer): CustomerWatchList
  def getContentsFromWatchList(customer: Customer): CustomerWatchList
}

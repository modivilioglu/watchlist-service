package com.mod.watchlist.service

import com.google.inject.Inject
import com.mod.watchlist.dao.WatchListDao

class WatchListServiceImpl @Inject()(dao: WatchListDao) extends WatchListService {
  override def addContentToWatchList(contents: List[Content], customer: Customer): CustomerWatchList = {
    dao.addToWatchList(contents, customer)
  }

  override def deleteContentFromWatchList(contents: List[Content], customer: Customer): CustomerWatchList = {
    dao.deleteFromWatchList(contents, customer)
  }

  override def getContentsFromWatchList(customer: Customer): CustomerWatchList = {
    dao.getWatchList(customer)
  }
}

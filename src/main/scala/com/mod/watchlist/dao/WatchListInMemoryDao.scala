package com.mod.watchlist.dao

import com.mod.watchlist.service.{Content, Customer, CustomerWatchList}

object WatchListInMemoryDao extends WatchListDao {
  private var watchListRepository: List[CustomerWatchList] = Nil

  def withInitialData: WatchListDao = {
    watchListRepository = List(CustomerWatchList(List(Content("CONT5")), Customer("CS1"))) //Initial Data, as if already present in DB
    this
  }

  override def getWatchList(customer: Customer): CustomerWatchList = {
    val maybeResult = watchListRepository.find(wl => wl.customer.id == customer.id)
    maybeResult.fold(CustomerWatchList(Nil, customer))(wl => wl)
  }

  override def addToWatchList(contents: List[Content], customer: Customer): CustomerWatchList = {
    val watchListToAdd = getWatchList(customer) |+| contents

    watchListRepository = watchListToAdd :: watchListRepository
    watchListToAdd
  }

  override def deleteFromWatchList(contents: List[Content], customer: Customer): CustomerWatchList = {
    val watchListOfCustomer = getWatchList(customer)

    val newWatchList = watchListOfCustomer |-| contents

    watchListRepository = newWatchList :: watchListRepository.filter(watchList => watchList.customer.id != customer.id)
    newWatchList
  }
}

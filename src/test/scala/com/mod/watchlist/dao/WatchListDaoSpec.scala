package com.mod.watchlist.dao

import com.mod.watchlist.service.{Content, Customer, CustomerWatchList}
import org.scalatest.{Matchers, WordSpec}

class WatchListDaoSpec extends WordSpec with Matchers {

  val dao = WatchListInMemoryDao.withInitialData

  trait SetUp {
    val content1 = Content("CONT1")
    val content5 = Content("CONT5")
    val customer = Customer("CS1")
  }
  "WatchListInMemoryDao" should {
    "add listing to repository" in new SetUp { // Cake Pattern
      val watchList = dao.addToWatchList(List(content1), customer)

      watchList shouldEqual (CustomerWatchList(List(content5, content1), customer))
    }

    "get the inserted listing from repository" in new SetUp {
      dao.getWatchList(customer) shouldEqual (CustomerWatchList(List(content5, content1), customer))
    }


    "delete the listing successfully from repository" in new SetUp {
      dao.deleteFromWatchList(List(content1), customer) shouldEqual (CustomerWatchList(List(content5), customer))
    }
  }

}

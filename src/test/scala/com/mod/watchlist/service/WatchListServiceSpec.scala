package com.mod.watchlist.service

import com.mod.watchlist.dao.WatchListDao
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}


class WatchListServiceSpec extends WordSpec with Matchers with MockitoSugar {
  trait SetUp { // Cake pattern applied
    val dao = mock[WatchListDao]
    val content = Content("CONT1")
    val customer = Customer("CS1")
    val newContent = Content("CONT2")
    val nonExistentCustomer = Customer("CS2")
    when(dao.getWatchList(customer))
      .thenReturn(CustomerWatchList(List(content), customer))

    when(dao.addToWatchList(List(newContent), customer))
      .thenReturn(CustomerWatchList(List(newContent, content), customer))

    when(dao.deleteFromWatchList(List(content), customer))
      .thenReturn(CustomerWatchList(List(newContent), customer))

    val service = new WatchListServiceImpl(dao)
  }

  "WatchListService" should {

    "get the customerWatchList from dao" in new SetUp {
      val result = service.getContentsFromWatchList(customer)
      result shouldEqual (CustomerWatchList(List(content), customer))
    }

    "add a new content to the customerWatchList" in new SetUp {

      val result = service.addContentToWatchList(List(newContent), customer)
      result shouldEqual (CustomerWatchList(List(newContent, content), customer))
    }

    "delete a content from the customerWatchList" in new SetUp {

      val result = service.deleteContentFromWatchList(List(content), customer)
      result shouldEqual (CustomerWatchList(List(newContent), customer))
    }

  }

}

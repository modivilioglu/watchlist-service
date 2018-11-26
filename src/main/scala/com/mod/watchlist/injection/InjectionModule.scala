package com.mod.watchlist.injection

import com.google.inject._
import com.mod.watchlist.dao._
import com.mod.watchlist.server.{WatchListApi, WatchListApiImpl}
import com.mod.watchlist.service.{AuthenticationService, AuthenticationServiceImpl, WatchListService, WatchListServiceImpl}

class InjectionModule extends AbstractModule {
  override def configure: Unit = {
    bind(classOf[WatchListDao]).toInstance(WatchListInMemoryDao.withInitialData)
    bind(classOf[CustomerDao]).toInstance(CustomerInMemoryDao.withInitialData)
    bind(classOf[WatchListService]).to(classOf[WatchListServiceImpl])
    bind(classOf[AuthenticationService]).to(classOf[AuthenticationServiceImpl])
    bind(classOf[WatchListApi]).to(classOf[WatchListApiImpl])
  }
}
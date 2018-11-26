package com.mod.watchlist.server

import akka.http.scaladsl.server.Route

trait WatchListApi {
  def routes: Route
}

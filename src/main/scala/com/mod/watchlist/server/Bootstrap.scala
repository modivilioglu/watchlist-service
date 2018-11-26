package com.mod.watchlist.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.mod.watchlist.injection.InjectionModule
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Bootstrap extends App {
  implicit val system: ActorSystem = ActorSystem("WatchlistServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val injector = Guice.createInjector(new InjectionModule)
  val watchListApi = injector.getInstance(classOf[WatchListApi])
  lazy val routes: Route = watchListApi.routes

  Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)

}

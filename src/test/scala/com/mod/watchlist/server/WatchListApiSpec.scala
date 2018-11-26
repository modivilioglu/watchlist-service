package com.mod.watchlist.server

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{ContentTypes, MessageEntity, StatusCodes}
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.google.inject.Guice
import com.mod.watchlist.injection.InjectionModule
import com.mod.watchlist.service._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class WatchListApiSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  val injector = Guice.createInjector(new InjectionModule)
  val watchListApi = injector.getInstance(classOf[WatchListApi])
  lazy val routes: Route = watchListApi.routes
  val customer1 = Customer("CS1")
  val customer2 = Customer("CS2")
  /*
  Acceptance Criteria
  • Customer can add contentIDs to their Watchlist
  • Customer can delete contentIDs from their Watchlist
  • Customer can see contents they added in their Watchlist
  • Customer cannot see another customer’s Watchlist
  • Each customer is represented by a unique 3 digit alphanumeric string
  • The Watchlist items should be stored in memory
  • The API should produce and consume JSON
  */

  "WatchListApi" should {
    // Acceptance Criteria: Customer can see contents they added in their Watchlist
    "return the inserted watchlist present (GET /api/v1/watchlist/customerId)" in {

      val validCredentials = BasicHttpCredentials("CS1", "p4ssw0rd")
      Get("/api/v1/watchlist/" + customer1.id) ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        val apiResult = entityAs[ApiResult]

        apiResult.watchList shouldEqual (CustomerWatchList(List(Content("CONT5")), customer1))
        apiResult.watchList.customer shouldEqual (customer1)
      }
    }
    // Acceptance Criteria: Customer cannot see another customer’s Watchlist
    "not allow other customers watclist viewed by 3rd person (GET /api/v1/watchlist/customerId)" in {

      val validCredentials = BasicHttpCredentials("CS1", "p4ssw0rd")
      Get("/api/v1/watchlist/" + customer2.id) ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.Unauthorized)
      }
    }

    // Acceptance Criteria: Customer can delete contentIDs from their Watchlist
    "delete content from watchlist (DELETE /api/v1/watchlist/customerId)" in {

      val contentToDelete = Content("CONT5")
      val validCredentials = BasicHttpCredentials("CS1", "p4ssw0rd")
      Delete("/api/v1/watchlist?ids=" + contentToDelete.id) ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.Accepted)

        contentType should ===(ContentTypes.`application/json`)

        val apiResult = entityAs[ApiResult]

        apiResult.watchList shouldEqual (CustomerWatchList(Nil, customer1))
        apiResult.watchList.customer shouldEqual (customer1)
      }
    }
    // Acceptance Criteria: Customer can add contentIDs to their Watchlist
    "add new content to watchlist (POST /api/v1/watchlist/customerId)" in {

      val contentToAdd = Content("CONT6")
      val validCredentials = BasicHttpCredentials("CS1", "p4ssw0rd")
      val contentEntity = Marshal(Contents(List(contentToAdd))).to[MessageEntity].futureValue

      Post("/api/v1/watchlist/").withEntity(contentEntity) ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.Created)

        contentType should ===(ContentTypes.`application/json`)

        val apiResult = entityAs[ApiResult]

        apiResult.watchList shouldEqual (CustomerWatchList(List(Content("CONT6")), customer1))
        apiResult.watchList.customer shouldEqual (customer1)
      }
    }

  }

  """Given a customer with id ‘123’ and an empty Watchlist
  When the customer adds ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’, ‘FBSxr’ to their watchlist
  Then their Watchlist is returned it """ should {
    "only include ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’, ‘FBSxr’" in {

      val validCredentials = BasicHttpCredentials("123", "p4ssw0rd")

      val contents: Contents = "zRE49,wYqiZ,15nW5,srT5k,FBSxr"

      val contentEntity = Marshal(contents).to[MessageEntity].futureValue
      Post("/api/v1/watchlist/").withEntity(contentEntity) ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.Created)

        contentType should ===(ContentTypes.`application/json`)
        val apiResult = entityAs[ApiResult]
        apiResult.watchList shouldEqual (CustomerWatchList(contents.contents, Customer("123")))
        apiResult.watchList.customer shouldEqual (Customer("123"))
      }
    }
  }

  """Given a customer with id ‘123’ and a Watchlist containing ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’,
  ‘FBSxr’
  When they remove ContentID ‘15nW5’ from their Watchlist
  Then their Watchlist """ should {
    "only contain ContentIDs ‘zRE49’, ‘wYqiZ’, ‘srT5k’, ‘FBSxr’" in {
      val validCredentials = BasicHttpCredentials("123", "p4ssw0rd")

      Delete("/api/v1/watchlist?ids=15nW5") ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.Accepted)

        contentType should ===(ContentTypes.`application/json`)
        val apiResult = entityAs[ApiResult]
        val contentsRemaining: Contents = "zRE49, wYqiZ, srT5k, FBSxr"
        apiResult.watchList shouldEqual (CustomerWatchList(contentsRemaining.contents, Customer("123")))
        apiResult.watchList.customer shouldEqual (Customer("123"))
      }
    }
  }

  """Given two customers, one with id ‘123’ and one with id ‘abc’
  And corresponding Watchlists containing ContentIDs ‘zRE49’, ‘wYqiZ’, ‘srT5k’, ‘FBSxr’ and ‘hWjNK’,
  ’U8jVg’, ‘GH4pD’, ’rGIha’ respectively
  When customer with id ‘abc’ views their Watchlist they""" should {
    "only see ContentIDs ‘hWjNK’, ’U8jVg’, ‘GH4pD’, ’rGIha’" in {
      val validCredentials = BasicHttpCredentials("abc", "p4ssw0rd")

      val contents: Contents = "hWjNK, U8jVg, GH4pD, rGIha"

      val contentEntity = Marshal(contents).to[MessageEntity].futureValue
      Post("/api/v1/watchlist/").withEntity(contentEntity) ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.Created)
      }

      Get("/api/v1/watchlist/123") ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.Unauthorized)
      }

      Get("/api/v1/watchlist/abc") ~> addCredentials(validCredentials) ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        val apiResult = entityAs[ApiResult]
        apiResult.watchList shouldEqual (CustomerWatchList(contents.contents, Customer("abc")))
        apiResult.watchList.customer shouldEqual (Customer("abc"))
      }

    }
  }
}

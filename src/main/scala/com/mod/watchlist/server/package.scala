package com.mod.watchlist

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.mod.watchlist.service.{Content, Customer, CustomerWatchList}
import spray.json.DefaultJsonProtocol
package object server extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val contentJsonFormat = jsonFormat1(Content)
  implicit val contentsJsonFormat = jsonFormat1(Contents)
  implicit val customerJsonFormat = jsonFormat1(Customer)
  implicit val customerWatchListJsonFormat = jsonFormat2(CustomerWatchList)
  implicit val apiResultJsonFormat = jsonFormat1(ApiResult)
  implicit val apiMessageJsonFormat = jsonFormat1(ApiMessage)
  implicit val apiErrorJsonFormat = jsonFormat3(ApiError)

  case class Contents(contents: List[Content])

  implicit def fromStringToContents(commaSeperated: String): Contents = {
    val contentList = commaSeperated.split(",").toList.map(id => Content(id.trim))
    Contents(contentList)
  }
  sealed trait RestResult
  case class ApiResult(watchList: CustomerWatchList) extends RestResult
  case class ApiMessage(message: String) extends RestResult
  case class ApiError(statusCode: String, errorMessage: String, errorDetails: String) extends RestResult
}

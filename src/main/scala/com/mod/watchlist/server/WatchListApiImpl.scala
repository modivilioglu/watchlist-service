package com.mod.watchlist.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.{AuthorizationFailedRejection, _}
import com.google.inject.Inject
import com.mod.watchlist.service._

class WatchListApiImpl @Inject()(authenticationService: AuthenticationService, service: WatchListService) extends WatchListApi {
  val FIXED_CONTENT_ID_SIZE = 5

  override def routes: Route = pathPrefix("api") {
    handleRejections(rejectionHandler) {
      pathPrefix("v1") {
        pathPrefix("watchlist") {
          authenticateBasic(realm = "secure site", authenticateCustomer) { customer =>
            concat(getWatchList(customer), addWatchList(customer), deleteWatchList(customer))
          }
        }
      }
    }
  }

  val rejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound {
      complete((StatusCodes.NotFound, ApiError("404", "Resource Not Found", "Resource Not Found")))
    }
    .handle {
      case ValidationRejection(msg, _) => complete(StatusCodes.BadRequest, ApiError(StatusCodes.BadRequest.intValue.toString, msg, msg))
      case AuthenticationFailedRejection(msg, _) => complete(StatusCodes.Forbidden, ApiError(StatusCodes.Forbidden.intValue.toString, msg.toString, msg.toString))
      case AuthorizationFailedRejection => complete(StatusCodes.Unauthorized, ApiError(StatusCodes.Unauthorized.intValue.toString, StatusCodes.Unauthorized.reason, StatusCodes.Forbidden.reason))
      case _ => complete(StatusCodes.BadRequest, ApiError(StatusCodes.BadRequest.intValue.toString, "Bad Request", "Unknown reason, may be regarding the input format"))
    }

    .result()

  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case ex: Exception =>
        complete((StatusCodes.InternalServerError, ApiError("500", ex.getMessage, "")))
  }

  private def authenticateCustomer(credentials: Credentials): Option[Customer] =
    credentials match {
      case Credentials.Provided(id) => authenticationService.authenticate(id)
      case _ => None
    }

  def authorizeCustomer(authenticatedCustomer: Customer, customerWithData: Customer): Boolean =
    authenticatedCustomer.id == customerWithData.id

  private def getWatchList(loggedInCustomer: Customer): Route = path(Segment) { id =>
    get {
      authorize(authorizeCustomer(loggedInCustomer, Customer(id))) {
        val watchList: CustomerWatchList = service.getContentsFromWatchList(Customer(id))
        if (watchList.list.isEmpty)
          complete((StatusCodes.NotFound, ApiError("404", "Entity Not Found", "Customer does not have a watchlist")))
        else
          complete(ApiResult(watchList))
      }
    }
  }

  private def addWatchList(customer: Customer): Route = entity(as[Contents]) { contents =>
    post {
      validate(contents.contents.forall(_.id.size == FIXED_CONTENT_ID_SIZE), s"Content ID should be 5 alphanumeric characters") {
        val watchList = service.addContentToWatchList(contents.contents, customer)
        complete((StatusCodes.Created, ApiResult(watchList)))
      }
    }
  }

  private def deleteWatchList(customer: Customer): Route =
    parameters('ids) { ids =>
    delete {
        val contentsToDelete = ids.split(",").toList.map(Content(_))
        val watchList = service.deleteContentFromWatchList(contentsToDelete, customer)
        complete((StatusCodes.Accepted, ApiResult(watchList)))
      }
    }
}


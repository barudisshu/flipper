package com.undeploy.oauth2

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions
import scalaoauth2.provider.ProtectedResource
import scalaoauth2.provider.TokenEndpoint
import scalaoauth2.provider.AuthorizationRequest
import scalaoauth2.provider.ProtectedResourceRequest
import scalaoauth2.provider.OAuthError
import scalaoauth2.provider.GrantHandlerResult
import scalaoauth2.provider.AuthorizationHandler
import scalaoauth2.provider.ProtectedResourceHandler
import scalaoauth2.provider.AuthInfo
import akka.actor.Actor
import spray.routing.HttpService
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.MediaTypes._
import spray.http.HttpEntity
import spray.http.HttpHeader
import spray.http.HttpChallenge
import spray.json.JsObject
import spray.json.JsString
import spray.json.JsNumber
import spray.json._
import spray.http.HttpRequest
import spray.http.CacheDirectives._
import spray.http.HttpHeaders._
import scalaoauth2.provider.DataHandler

trait OAuth2HttpService[U] extends HttpService {

  val dataHandler: DataHandler[U]

  val protectedResource: ProtectedResource = ProtectedResource

  val tokenEndpoint: TokenEndpoint = TokenEndpoint

  object OAuth2JsonProtocol extends DefaultJsonProtocol {
    implicit val grantHandlerResultFormat = jsonFormat5(GrantHandlerResult)
  }

  import OAuth2JsonProtocol._
  def issueAccessToken()(implicit request: HttpRequest, ctx: ExecutionContext): Future[HttpResponse] = {
    tokenEndpoint.handleRequest(request, dataHandler).map {
      case Left(e) => HttpResponse(
        e.statusCode,
        responseOAuthErrorJson(e),
        responseOAuthErrorHeader(e))
      case Right(r) => HttpResponse(
        entity = HttpEntity(
          `application/json`,
          r.toJson.toString()))
        .withHeaders(`Cache-Control`(`no-store`))
    }
  }

  def authorize(callback: AuthInfo[U] => Future[HttpResponse])(implicit request: HttpRequest, ctx: ExecutionContext): Future[HttpResponse] = {
    protectedResource.handleRequest(request, dataHandler).flatMap {
      case Left(e)         => Future.successful(HttpResponse(e.statusCode, headers = responseOAuthErrorHeader(e)))
      case Right(authInfo) => callback(authInfo)
    }
  }

  val oauth2 =
    path("oauth2" / "access_token") {
      post {
        entity(as[HttpRequest]) { implicit req =>
          complete {
            issueAccessToken()
          }
        }
      }
    }

  def extractHeadersAndParams(req: HttpRequest) = {
    val headers = req.headers
      .map(e => e.name -> e.value)
      .groupBy(_._1)
      .map { case (k, v) => (k, v.map(_._2)) }
    val queryParams = req.uri.query.toMultiMap
    (headers, queryParams)
  }

  implicit def play2oauthRequest(request: HttpRequest): AuthorizationRequest = {
    val headersAndParams = extractHeadersAndParams(request)
    AuthorizationRequest(headersAndParams._1, headersAndParams._2)
  }

  implicit def play2protectedResourceRequest(request: HttpRequest): ProtectedResourceRequest = {
    val headersAndParams = extractHeadersAndParams(request)
    ProtectedResourceRequest(headersAndParams._1, headersAndParams._2)
  }

  def responseOAuthErrorJson(e: OAuthError): HttpEntity = {
    val json = JsObject(
      "error" -> JsString(e.errorType),
      "error_description" -> JsString(e.description))
    HttpEntity.apply(`application/json`, json.toString)
  }

  import spray.http.HttpHeaders._
  def responseOAuthErrorHeader(e: OAuthError): List[HttpHeader] =
    List(`WWW-Authenticate`(HttpChallenge("Bearer ", toOAuthErrorString(e))))

  protected def toOAuthErrorString(e: OAuthError): String = {
    val params = Seq("error=\"" + e.errorType + "\"") ++
      (if (!e.description.isEmpty) { Seq("error_description=\"" + e.description + "\"") } else { Nil })
    params.mkString(", ")
  }

}
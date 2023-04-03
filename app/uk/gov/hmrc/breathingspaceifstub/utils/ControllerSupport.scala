/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.breathingspaceifstub.utils

import java.util.UUID

import scala.concurrent.Future
import scala.io.Source
import scala.util.Try

import play.api.Logging
import play.api.http.Status._
import play.api.libs.json._
import play.api.mvc.{Request, Result, Results}
import play.mvc.Http.MimeTypes
import uk.gov.hmrc.breathingspaceifstub.Header

trait ControllerSupport extends Results with Logging {

  def failures(code: String, reason: String = "A generic error"): JsValue =
    Json.parse(s"""{"failures":[{"code":"$code","reason":"$reason"}]}""")

  def sendResponse(httpCode: Int, body: JsValue)(implicit request: Request[_]): Future[Result] =
    Future.successful {
      Status(httpCode)(body)
        .withHeaders(
          Header.CorrelationId -> request.headers
            .get(Header.CorrelationId)
            .getOrElse(UUID.randomUUID().toString)
        )
        .as(MimeTypes.JSON)
    }

  def composeResponse(nino: String, acceptedHandler: (String) => Future[Result])(
    implicit request: Request[_]
  ): Future[Result] = {
    val normalisedNino = nino.toUpperCase.take(8)
    if (normalisedNino.take(2) == "BS") sendErrorResponseFromNino(normalisedNino) // a bad nino
    else acceptedHandler(normalisedNino)
  }

  def getDataFromFile(filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/$filename")
    Source.fromInputStream(in).getLines.mkString
  }

  def getJsonDataFromFile(filename: String): JsValue = {
    val in = getClass.getResourceAsStream(s"/data/$filename")
    val raw = Source.fromInputStream(in).getLines.mkString
    Json.parse(raw)
  }

  private def sendErrorResponseFromNino(nino: String)(implicit request: Request[_]): Future[Result] = {
    val statusCode = Try(nino.substring(5, 8).toInt).getOrElse(INTERNAL_SERVER_ERROR)
    httpErrorCodes
      .get(statusCode)
      .fold(sendResponse(INTERNAL_SERVER_ERROR, failures("SERVER_ERROR"))) { code =>
        sendResponse(statusCode, failures(code))
      }
  }

  lazy val httpErrorCodes = Map(
    400 -> "BAD_REQUEST",
    401 -> "UNAUTHORIZED",
    402 -> "PAYMENT_REQUIRED",
    403 -> "BREATHINGSPACE_EXPIRED",
    404 -> "RESOURCE_NOT_FOUND",
    405 -> "METHOD_NOT_ALLOWED",
    406 -> "NOT_ACCEPTABLE",
    407 -> "PROXY_AUTHENTICATION_REQUIRED",
    408 -> "REQUEST_TIMEOUT",
    409 -> "CONFLICTING_REQUEST",
    410 -> "GONE",
    411 -> "LENGTH_REQUIRED",
    412 -> "PRECONDITION_FAILED",
    413 -> "REQUEST_ENTITY_TOO_LARGE",
    414 -> "REQUEST_URI_TOO_LONG",
    415 -> "MISSING_JSON_HEADER",
    416 -> "REQUESTED_RANGE_NOT_SATISFIABLE",
    417 -> "EXPECTATION_FAILED",
    422 -> "UNKNOWN_DATA_ITEM",
    423 -> "LOCKED",
    424 -> "FAILED_DEPENDENCY",
    426 -> "UPGRADE_REQUIRED",
    428 -> "HEADERS_PRECONDITION_NOT_MET",
    429 -> "TOO_MANY_REQUESTS",
    500 -> "SERVER_ERROR",
    501 -> "NOT_IMPLEMENTED",
    502 -> "BAD_GATEWAY",
    503 -> "SERVICE_UNAVAILABLE",
    504 -> "GATEWAY_TIMEOUT",
    505 -> "HTTP_VERSION_NOT_SUPPORTED",
    507 -> "INSUFFICIENT_STORAGE",
    511 -> "NETWORK_AUTHENTICATION_REQUIRED"
  )
}

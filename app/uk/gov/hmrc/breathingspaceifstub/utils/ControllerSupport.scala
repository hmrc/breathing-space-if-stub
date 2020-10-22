/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.mvc.Http.MimeTypes
import uk.gov.hmrc.breathingspaceifstub.Header

trait ControllerSupport extends Results with Logging {
  def sendResponse(httpCode: Int, responseBody: Option[JsValue] = None)(
    implicit request: Request[AnyContent]
  ): Future[Result] = {
    val body = responseBody.getOrElse(Json.obj("response" -> s"MDTP IF Stub returning '${httpCode}' as requested"))

    Future.successful(
      Status(httpCode)(body)
        .withHeaders(
          Header.CorrelationId -> request.headers
            .get(Header.CorrelationId)
            .getOrElse(UUID.randomUUID().toString)
        )
        .as(MimeTypes.JSON)
    )
  }

  def extractErrorStatusFromNino(nino: String): Int = {
    val requestedResponseCode = Try(nino.substring(5, 8).toInt).getOrElse(INTERNAL_SERVER_ERROR)
    if (requestedResponseCode < 200 || requestedResponseCode > 599) INTERNAL_SERVER_ERROR else requestedResponseCode
  }

  def composeResponse(nino: String, acceptedHandler: (String) => Future[Result])(
    implicit request: Request[AnyContent]
  ): Future[Result] = {
    val normalisedNino = nino.toUpperCase.take(8)

    normalisedNino.take(2) match {
      case "BS" => // a bad nino
        sendResponse(extractErrorStatusFromNino(normalisedNino))

      case _ => acceptedHandler(normalisedNino)
    }
  }

  def getJsonDataFromFile(filename: String): JsValue = {
    val in = getClass.getResourceAsStream(s"/data/$filename")
    val raw = Source.fromInputStream(in).getLines.mkString
    Json.parse(raw)
  }
}

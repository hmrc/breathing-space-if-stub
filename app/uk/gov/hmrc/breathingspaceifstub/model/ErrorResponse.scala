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

package uk.gov.hmrc.breathingspaceifstub.model

import scala.concurrent.Future

import cats.data.NonEmptyChain
import play.api.Logging
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.MimeTypes
import play.api.libs.json._
import play.api.mvc.Result
import play.api.mvc.Results.Status
import uk.gov.hmrc.breathingspaceifstub.Header

case class ErrorResponse(value: Future[Result])

object ErrorResponse extends Logging {

  type Errors = NonEmptyChain[Error]

  def apply(httpErrorCode: Int, error: Error)(implicit correlationId: CorrelationId): ErrorResponse = {
    val payload = Json.obj("failures" -> List(error))
    logger.error(correlationId.value.fold(payload.toString)(cid => s"(Correlation-id: $cid) ${payload.toString}"))
    errorResponse(correlationId, httpErrorCode, payload)
  }

  def apply(httpErrorCode: Int, errors: Errors)(implicit correlationId: CorrelationId): ErrorResponse = {
    val payload = Json.obj("failures" -> errors.toChain.toList)
    logger.error(correlationId.value.fold(payload.toString)(cid => s"(Correlation-id: $cid) ${payload.toString}"))
    errorResponse(correlationId, httpErrorCode, payload)
  }

  def apply(
    httpErrorCode: Int,
    reasonToLog: => String,
    throwable: Throwable
  )(implicit correlationId: CorrelationId): ErrorResponse = {
    logger.error(correlationId.value.fold(reasonToLog)(corrId => s"(Correlation-id: $corrId) $reasonToLog"), throwable)
    val payload = Json.obj("failures" -> Error.fromThrowable(throwable))
    errorResponse(correlationId, httpErrorCode, payload)
  }

  private def errorResponse(correlationId: CorrelationId, httpErrorCode: Int, payload: JsObject): ErrorResponse = {
    val headers = List(CONTENT_TYPE -> MimeTypes.JSON)
    new ErrorResponse(Future.successful {
      Status(httpErrorCode)(payload)
        .withHeaders(correlationId.value.fold(headers)(cid => headers :+ (Header.CorrelationId -> cid)): _*)
    })
  }
}

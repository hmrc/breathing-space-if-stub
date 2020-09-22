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

package uk.gov.hmrc.breathingspaceifstub.controller

import java.util.UUID

import cats.data.ValidatedNec
import cats.implicits._
import play.api.Logging
import play.api.http.{HttpVerbs, MimeTypes}
import play.api.libs.json._
import play.api.mvc.{BaseController => PlayController, _}
import uk.gov.hmrc.breathingspaceifstub._
import uk.gov.hmrc.breathingspaceifstub.model.{Attended, CorrelationId, Error, Nino}
import uk.gov.hmrc.breathingspaceifstub.model.Error._
import uk.gov.hmrc.domain.{Nino => DomainNino}

trait RequestValidation extends PlayController with Logging {

  type Validation[A] = ValidatedNec[Error, A]

  def validateNino(maybeNino: String): Validation[Nino] =
    if (DomainNino.isValid(maybeNino)) Nino(maybeNino).validNec
    else INVALID_PAYLOAD.invalidNec

  def validateHeaders(implicit request: Request[_]): Validation[Unit] = {
    val headers = request.headers
    (
      validateContentType(request),
      validateCorrelationId(headers),
      validateRequestType(headers),
      validateStaffId(headers)
    ).mapN((_, _, _, _) => unit)
  }

  def validateBody[T](implicit request: Request[AnyContent], reads: Reads[T]): Validation[T] =
    if (!request.hasBody) INVALID_PAYLOAD.invalidNec
    else {
      request.body.asJson.fold[Validation[T]](INVALID_PAYLOAD.invalidNec) { json =>
        Either.catchNonFatal(json.as[T]).fold(createErrorFromInvalidPayload[T], _.validNec)
      }
    }

  private def validateContentType(request: Request[_]): Validation[Unit] =
    request.headers
      .get(CONTENT_TYPE)
      .fold[Validation[Unit]] {
        if (request.method.toUpperCase == HttpVerbs.GET) unit.validNec
        // The "Content-type" header is mandatory for POST, PUT and DELETE,
        // (they are the only HTTP methods accepted, with GET of course).
        else INVALID_PAYLOAD.invalidNec
      } { contentType =>
        // In case the "Content-type" header is specified, a body,
        // if any, is always expected to be in Json format.
        if (contentType.toLowerCase == MimeTypes.JSON.toLowerCase) unit.validNec
        else INVALID_PAYLOAD.invalidNec
      }

  private def validateCorrelationId(headers: Headers): Validation[Unit] =
    headers
      .get(Header.CorrelationId)
      .fold[Validation[Unit]] {
        INVALID_CORRELATIONID.invalidNec
      } { correlationId =>
        Either
          .catchNonFatal(UUID.fromString(correlationId))
          .fold[Validation[Unit]](
            _ => INVALID_CORRELATIONID.invalidNec,
            _ => unit.validNec
          )
      }

  private def validateRequestType(headers: Headers): Validation[Unit] =
    headers
      .get(Header.OriginatorId)
      .fold[Validation[Unit]] {
        INVALID_ORIGINATORID.invalidNec
      } { requestType =>
        Attended
          .withNameOption(requestType.toUpperCase)
          .fold[Validation[Unit]] {
            INVALID_ORIGINATORID.invalidNec
          } { _ =>
            unit.validNec
          }
      }

  private val staffIdRegex = "^[0-9]{7}$".r

  private def validateStaffId(headers: Headers): Validation[Unit] =
    headers
      .get(Header.UserId)
      .fold[Validation[Unit]] {
        INVALID_USERID.invalidNec
      } { staffId =>
        staffIdRegex
          .findFirstIn(staffId)
          .fold[Validation[Unit]] {
            INVALID_USERID.invalidNec
          } { _ =>
            unit.validNec
          }
      }

  private def createErrorFromInvalidPayload[B](throwable: Throwable)(implicit request: Request[_]): Validation[B] = {
    val correlationId = implicitly[CorrelationId]
    val reason = s"Exception raised while validating the request's body: ${request.body}"
    logger.error(s"(Correlation-id: ${correlationId.value.fold("")(_)}) $reason", throwable)
    INVALID_PAYLOAD.invalidNec
  }
}

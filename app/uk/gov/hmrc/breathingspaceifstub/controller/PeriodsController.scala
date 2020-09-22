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

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc._
import play.mvc.Http.MimeTypes
import uk.gov.hmrc.breathingspaceifstub.{Header, Periods}
import uk.gov.hmrc.breathingspaceifstub.model._
import uk.gov.hmrc.breathingspaceifstub.model.Error.RESOURCE_NOT_FOUND
import uk.gov.hmrc.breathingspaceifstub.repository.{DebtorRepository, PeriodsRepository}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton()
class PeriodsController @Inject()(
  cc: ControllerComponents,
  debtorRepo: DebtorRepository,
  periodsRepo: PeriodsRepository
)(implicit val ec: ExecutionContext)
    extends BackendController(cc)
    with RequestValidation {

  def post(maybeNino: String): Action[AnyContent] = Action.async { implicit request =>
    (
      validateHeaders,
      validateNino(maybeNino),
      validateBody[CreatePeriodsRequest]
    ).mapN((_, nino, cpr) => nino -> cpr)
      .fold(ErrorResponse(BAD_REQUEST, _).value, kv => processPost(kv._1, kv._2))
  }

  def processPost(nino: Nino, cpr: CreatePeriodsRequest)(implicit request: Request[_]): Future[Result] = {
    logger.debug(s"Adding Periods $cpr for Nino($nino)")
    debtorRepo
      .get(nino, periodsRepo.add(nino, cpr.periods.map(Period(_))))
      .fold(ErrorResponse(NOT_FOUND, RESOURCE_NOT_FOUND).value)(sendResponse(CREATED, _))
  }

  def sendResponse(httpCode: Int, periods: Periods)(implicit correlationId: CorrelationId): Future[Result] =
    Future.successful {
      Status(httpCode)(Json.obj("periods" -> Json.toJson(periods)))
        .withHeaders(Header.CorrelationId -> correlationId.value.fold("(BUG) Not Found?")(identity))
        .as(MimeTypes.JSON)
    }
}

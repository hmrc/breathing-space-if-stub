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

import java.time.{LocalDate, ZonedDateTime}

import cats.syntax.option._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.breathingspaceifstub.Periods
import uk.gov.hmrc.breathingspaceifstub.model.RequestPeriod
import uk.gov.hmrc.breathingspaceifstub.repository.PeriodsRepository
import uk.gov.hmrc.breathingspaceifstub.support.BaseISpec

class PeriodsControllerISpec extends BaseISpec {

  val periodsRepo = app.injector.instanceOf[PeriodsRepository]
  val periodsStore = periodsRepo.store

  "POST /v1/:nino/periods" should {

    "return 201(CREATED) with the new periods when the Nino is valid and found in the DB" in {
      val nino = periodsStore.head._1
      val requestPeriod = RequestPeriod(LocalDate.now, LocalDate.now.some, ZonedDateTime.now)
      val requestPeriods = Json.obj("periods" -> Json.toJson(List(requestPeriod)))
      val request = requestWithHeaders(POST, s"$localContext/v1/${nino.nino}/periods")
        .withJsonBody(requestPeriods)

      val Some(result) = route(app, request)
      status(result) shouldBe Status.CREATED

      val period = (contentAsJson(result) \ "periods").as[Periods].head
      period.startDate shouldBe requestPeriod.startDate
      period.endDate.some shouldBe requestPeriod.endDate
    }

    "return 201(CREATED) with an empty 'periods' list when the Nino is valid and found in the DB with same periods provided" in {
      val nino = periodsStore.head._1
      val expectedPeriods = periodsStore.head._2
      val requestPeriods = Json.obj("periods" -> Json.toJson(expectedPeriods.map(RequestPeriod(_))))
      val request = requestWithHeaders(POST, s"$localContext/v1/${nino.nino}/periods")
        .withJsonBody(requestPeriods)

      val Some(result) = route(app, request)
      status(result) shouldBe Status.CREATED

      (contentAsJson(result) \ "periods").get.toString shouldBe "[]"
    }

    "return 404(NOT_FOUND) the Nino is valid but was not found in the DB" in {
      val ignored = periodsStore.head._2
      val requestPeriods = Json.obj("periods" -> Json.toJson(ignored.map(RequestPeriod(_))))
      val request = requestWithHeaders(POST, s"$localContext/v1/MZ006527C/periods")
        .withJsonBody(requestPeriods)

      val Some(result) = route(app, request)
      status(result) shouldBe Status.NOT_FOUND
    }
  }
}

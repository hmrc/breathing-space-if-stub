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

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.breathingspaceifstub.repository.DebtorRepository

class BreathingSpaceControllerISpec extends BaseControllerISpec {

  "GET /debtor/:nino" should {
    "return NotFound(404) when the Nino is unknown" in {
      val result = await(ws.url(s"http://localhost:$port/breathing-space-if-stub/debtor/OT123456B").get)
      result.status shouldBe Status.NOT_FOUND
    }

    "return UnprocessableEntity(422) when the Nino is invalid" in {
      val result = await(ws.url(s"http://localhost:$port/breathing-space-if-stub/debtor/1234").get)
      result.status shouldBe Status.UNPROCESSABLE_ENTITY
    }

    "return debtor details when the Nino is valid" in {
      val nino = DebtorRepository.debtors.head._1
      val debtor = DebtorRepository.debtors.head._2
      val result = await(ws.url(s"http://localhost:$port/breathing-space-if-stub/debtor/${nino}").get)
      result.status shouldBe Status.OK
      Json.parse(result.body) shouldBe debtor
    }
  }

  "POST to /breathing-space/flow-6" should {
    "return 200" in {
      val data = Json.obj()
      val result = await(ws.url(s"http://localhost:$port/breathing-space-if-stub/flow-6")
        .addHttpHeaders("Content-Type" -> "application/json")
        .post(data))

      result.status shouldBe Status.OK
      val output = result.body.drop(1).dropRight(1)
      UUID.fromString(output) shouldBe a[UUID]
    }

    "return 400" in {
      val result = await(ws.url(s"http://localhost:$port/breathing-space-if-stub/flow-6")
        .addHttpHeaders("Content-Type" -> "application/json")
        .post(""))

      result.status shouldBe Status.BAD_REQUEST
    }

    "return 415" in {
      val result = await(ws.url(s"http://localhost:$port/breathing-space-if-stub/flow-6")
        .post(""))

      result.status shouldBe Status.UNSUPPORTED_MEDIA_TYPE
    }
  }
}

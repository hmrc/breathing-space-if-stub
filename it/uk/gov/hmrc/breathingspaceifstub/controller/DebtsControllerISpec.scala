/*
 * Copyright 2022 HM Revenue & Customs
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
import scala.io.Source
import play.api.http.Status
import play.api.test.Helpers.await
import uk.gov.hmrc.breathingspaceifstub.Header
import uk.gov.hmrc.breathingspaceifstub.model.CorrelationId
import uk.gov.hmrc.breathingspaceifstub.support.{BaseISpec, ControllerBehaviours}

class DebtsControllerISpec extends BaseISpec with ControllerBehaviours {

  implicit val correlationHeaderValue: CorrelationId = CorrelationId(Some(correlationId))

  "GET /NINO/:nino/debts" should {

    behave like aNinoAsErrorCodeEndpoint(s => makeGetRequest(getConnectionUrl(s)))
    behave like acceptsCorrelationId(makeGetRequest(getConnectionUrl("AS000001A")))
    behave like ninoSuffixIgnored(s => makeGetRequest(getConnectionUrl(s)))

    "return 200(OK) with a single debt (full population) when the Nino 'AS000001A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("singleBsDebtFullPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with a single debt (partial population) when the Nino 'AS000002A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000002A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("singleBsDebtPartialPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple debts (all full population) when the Nino 'AS000003A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000003A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsDebtsFullPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple debts (all partial population) when the Nino 'AS000004A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000004A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsDebtsPartialPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple debts (mixed population) when the Nino 'AS000005A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000005A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsDebtsMixedPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the url does not include the periodId" in {
      val connectionUrl = s"${testServerAddress}/individuals/breathing-space/NINO/AS000005A/debts"
      val response = makeGetRequest(connectionUrl)
      response.status shouldBe Status.NOT_FOUND
    }

    "return 400(BAD_REQUEST) when the periodId is not a valid UUID" in {
      val connectionUrl = s"${testServerAddress}/individuals/breathing-space/NINO/AS000005A/abc/debts"
      val response = makeGetRequest(connectionUrl)
      response.status shouldBe Status.BAD_REQUEST
    }

    "return 404(NO_DATA_FOUND) when the Nino specified is unknown " in {
      withClue("MA000700A") {
        val response = makeGetRequest(getConnectionUrl("MA000700A"))
        response.status shouldBe Status.NOT_FOUND
        assert(response.body.startsWith("""{"failures":[{"code":"NO_DATA_FOUND","reason":"""))
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }
  }

  private def getConnectionUrl(nino: String): String =
    s"${testServerAddress}/individuals/breathing-space/NINO/${nino}/${UUID.randomUUID}/debts"

  private def getExpectedResponseBody(filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/debts/$filename")
    Source.fromInputStream(in)
      .getLines
      .map( // remove pre padding whitespace & post colon whitespace from each line (but not whitespaces from values)
        _.replaceAll("^[ \\t]+", "")
         .replaceAll(":[ \\t]+", ":")
      )
      .mkString
  }
}

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

import play.api.http.Status
import play.api.test.Helpers.await
import uk.gov.hmrc.breathingspaceifstub.Header
import uk.gov.hmrc.breathingspaceifstub.model.CorrelationId
import uk.gov.hmrc.breathingspaceifstub.support.{BaseISpec, ControllerBehaviours}

import java.util.UUID
import scala.io.Source

class MemorandumControllerISpec extends BaseISpec with ControllerBehaviours {

  implicit val correlationHeaderValue: CorrelationId = CorrelationId(Some(correlationId))

  "GET /NINO/:nino/memorandum" should {

    behave like aNinoAsErrorCodeEndpoint(s => makeGetRequest(getConnectionUrl(s)))
    behave like acceptsCorrelationId(makeGetRequest(getConnectionUrl("AS000001A")))
    behave like ninoSuffixIgnored(s => makeGetRequest(getConnectionUrl(s)))

    "return 200(OK) with breathing space indicator true when the Nino 'AS000001A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("hasBreathingSpaceIndicator.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with breathing space indicator false when the Nino 'AS000002A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000002A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("noBreathingSpaceIndicator.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 404(NOT_FOUND) when the Nino 'AS000003A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000003A"))
      response.status shouldBe Status.NOT_FOUND
    }
  }

  private def getConnectionUrl(nino: String): String =
    s"${testServerAddress}/individuals/breathing-space/NINO/${nino}/memorandum"

  private def getExpectedResponseBody(filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/memorandum/$filename")
    Source.fromInputStream(in)
      .getLines
      .map( // remove pre padding whitespace & post colon whitespace from each line (but not whitespaces from values)
        _.replaceAll("^[ \\t]+", "")
         .replaceAll(":[ \\t]+", ":")
      )
      .mkString
  }
}

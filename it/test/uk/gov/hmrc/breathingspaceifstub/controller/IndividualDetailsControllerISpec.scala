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

package uk.gov.hmrc.breathingspaceifstub.controller

import play.api.http.Status
import uk.gov.hmrc.breathingspaceifstub.Header
import uk.gov.hmrc.breathingspaceifstub.controller.IndividualDetailsController._
import uk.gov.hmrc.breathingspaceifstub.model.CorrelationId
import uk.gov.hmrc.breathingspaceifstub.support.{BaseISpec, ControllerBehaviours}

import scala.io.Source

class IndividualDetailsControllerISpec extends BaseISpec with ControllerBehaviours {

  implicit val correlationHeaderValue: CorrelationId = CorrelationId(Some(correlationId))

  "GET /NINO/:nino" should {

    behave.like(aNinoAsErrorCodeEndpoint(s => makeGetRequest(getConnectionUrl(s, Some(filter)))))
    behave.like(acceptsCorrelationId(makeGetRequest(getConnectionUrl("AS000001A"))))
    behave.like(ninoSuffixIgnored(s => makeGetRequest(getConnectionUrl(s, Some(filter)))))

    "return 200(OK) with the expected individual details when the Url provides the expected filter" in {
      val nino = "AS000001"
      val response = makeGetRequest(getConnectionUrl(nino, Some(filter)))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody(nino, detailsForBreathingSpace)
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with a individual details (full population) when the Url does not provide a filter" in {
      val nino = "AS000001"
      val response = makeGetRequest(getConnectionUrl(nino, None))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody(nino, fullPopulationDetails)
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 422(UNPROCESSABLE_ENTITY) when the Url provides an unexpected filter" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A", Some("details(nino,dateOfBirth,cnrIndicator)")))
      response.status shouldBe Status.UNPROCESSABLE_ENTITY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }
  }

  private def getConnectionUrl(nino: String, filter: Option[String] = None): String = {
    val queryString = filter.map(value => s"?fields=${value}").getOrElse("")
    s"${testServerAddress}/individuals/details/NINO/${nino}${queryString}"
  }

  private def getExpectedResponseBody(nino: String, filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/individuals/$filename")
    Source
      .fromInputStream(in)
      .getLines
      .map( // remove pre padding whitespace & post colon whitespace from each line (but not whitespaces from values)
        _.replaceAll("^[ \\t]+", "")
          .replaceAll(":[ \\t]+", ":")
          .replaceFirst("\\$\\{nino}", nino)
      )
      .mkString
  }
}

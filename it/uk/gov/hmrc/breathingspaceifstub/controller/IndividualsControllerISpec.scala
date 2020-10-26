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

import scala.io.Source

import play.api.http.Status
import play.api.test.Helpers.await
import uk.gov.hmrc.breathingspaceifstub.Header
import uk.gov.hmrc.breathingspaceifstub.model.CorrelationId
import uk.gov.hmrc.breathingspaceifstub.support.BaseISpec

class IndividualsControllerISpec extends BaseISpec {

  implicit val correlationHeaderValue: CorrelationId = CorrelationId(Some(correlationId))

  "GET /NINO/:nino" should {
    "return 200(OK) with a individual details (minimum population) when the Nino 'AS000001A' is sent with correct filter" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A", Some("details(nino,dateOfBirth,cnrIndicator)")))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("individualMinimumPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 422(UNPROCESSABLE_ENTITY) with a individual details (minimum population) when the Nino 'AS000001A' is sent with missing filter" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A"))
      response.status shouldBe Status.UNPROCESSABLE_ENTITY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 422(UNPROCESSABLE_ENTITY) with a individual details (minimum population) when the Nino 'AS000001A' is sent with incorrect filter" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A", Some("!details(nino,dateOfBirth,cnrIndicator)")))
      response.status shouldBe Status.UNPROCESSABLE_ENTITY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }
    
    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 404(NOT_FOUND) when the Nino 'CS000404B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("CS000404B"))
      response.status shouldBe Status.NOT_FOUND
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS000500B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000500B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS0005R0B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS0005R0B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 502(BAD_GATEWAY) when the Nino 'BS000502B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000502B"))
      response.status shouldBe Status.BAD_GATEWAY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 503(SERVICE_UNAVAILABLE) when the Nino 'BS000503B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000503B"))
      response.status shouldBe Status.SERVICE_UNAVAILABLE
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino specifies a non-existing HTTP status code" in {
      val response = makeGetRequest(getConnectionUrl("BS000700B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "ensure Nino suffix is ignored" in {
      withClue("With suffix") {
        val response = makeGetRequest(getConnectionUrl("AS000001A", Some("details(nino,dateOfBirth,cnrIndicator)")))
        response.status shouldBe Status.OK
        response.body shouldBe getExpectedResponseBody("individualMinimumPopulation.json")
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Without suffix") {
        val response = makeGetRequest(getConnectionUrl("AS000001", Some("details(nino,dateOfBirth,cnrIndicator)")))
        response.status shouldBe Status.OK
        response.body shouldBe getExpectedResponseBody("individualMinimumPopulation.json")
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }

    "return same CorrelationId as sent regardless of header name's letter case" in {
      withClue("Mixed case") {
        val response = await(wsClient.url(getConnectionUrl("AS000001", Some("details(nino,dateOfBirth,cnrIndicator)")))
          .withHttpHeaders("CorrelationId" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Lower case") {
        val response = await(wsClient.url(getConnectionUrl("AS000001", Some("details(nino,dateOfBirth,cnrIndicator)")))
          .withHttpHeaders("correlationid" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Upper case") {
        val response = await(wsClient.url(getConnectionUrl("AS000001", Some("details(nino,dateOfBirth,cnrIndicator)")))
          .withHttpHeaders("CORRELATIONID" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }
  }

  private def makeGetRequest(connectionUrl: String)(implicit correlationId: CorrelationId) = {
    await(wsClient.url(connectionUrl)
      .withHttpHeaders(Header.CorrelationId -> correlationId.value.get)
      .get())
  }

  private def getConnectionUrl(nino: String, fields: Option[String] = None): String = {
    val querySting = fields.map(value => s"?fields=${value}").getOrElse("")
    s"${testServerAddress}/individuals/details/NINO/${nino}${querySting}"
  }

  private def getExpectedResponseBody(filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/individuals/$filename")
    Source.fromInputStream(in)
      .getLines
      .map( // remove pre padding whitespace & post colon whitespace from each line (but not whitespaces from values)
        _.replaceAll("^[ \\t]+", "").replaceAll(":[ \\t]+", ":"))
      .mkString
  }
}

/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.test.Helpers.await
import uk.gov.hmrc.breathingspaceifstub.Header
import uk.gov.hmrc.breathingspaceifstub.model.CorrelationId
import uk.gov.hmrc.breathingspaceifstub.support.BaseISpec

class PeriodsControllerISpec extends BaseISpec {

  implicit val correlationHeaderValue: CorrelationId = CorrelationId(Some(correlationId))

  "GET /NINO/:nino/periods" should {
    "return 200(OK) with a single period (full population) when the Nino 'AS000001A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000001A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("singleBsPeriodFullPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with a single period (partial population) when the Nino 'AS000002A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000002A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("singleBsPeriodPartialPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple periods (all full population) when the Nino 'AS000003A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000003A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsPeriodsFullPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple periods (all partial population) when the Nino 'AS000004A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000004A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsPeriodsPartialPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple periods (mixed population) when the Nino 'AS000005A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("AS000005A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsPeriodsMixedPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 404(NOT_FOUND) when the Nino 'BS000404B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000404B"))
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

    "return 200(OK) when the Nino specified is unknown " in {
      withClue("MA000700A") {
        val response = makeGetRequest(getConnectionUrl("MA000700A"))
        response.status shouldBe Status.OK
        response.body shouldBe """{"periods":[]}"""
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("MA000200B") {
        val response = makeGetRequest(getConnectionUrl("MA000200A"))
        response.status shouldBe Status.OK
        response.body shouldBe """{"periods":[]}"""
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("AB000500D") {
        val response = makeGetRequest(getConnectionUrl("AB000500D"))
        response.status shouldBe Status.OK
        response.body shouldBe """{"periods":[]}"""
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }

    "ensure Nino suffix is ignored" in {
      withClue("With suffix") {
        val response = makeGetRequest(getConnectionUrl("AS000001A"))
        response.status shouldBe Status.OK
        response.body shouldBe getExpectedResponseBody("singleBsPeriodFullPopulation.json")
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Without suffix") {
        val response = makeGetRequest(getConnectionUrl("AS000001"))
        response.status shouldBe Status.OK
        response.body shouldBe getExpectedResponseBody("singleBsPeriodFullPopulation.json")
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }

    "return same CorrelationId as sent regardless of header name's letter case" in {
      withClue("Mixed case") {
        val response = await(wsClient.url(getConnectionUrl("MA000700A"))
          .withHttpHeaders("CorrelationId" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Lower case") {
        val response = await(wsClient.url(getConnectionUrl("MA000700A"))
          .withHttpHeaders("correlationid" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("Upper case") {
        val response = await(wsClient.url(getConnectionUrl("MA000700A"))
          .withHttpHeaders("CORRELATIONID" -> correlationHeaderValue.value.get)
          .get())
        response.status shouldBe Status.OK
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }
  }

  "POST /NINO/:nino/periods" should {
    "return 201(CREATED) with the periods sent when any accepted Nino value is sent" in {
      val period1 = """{"startDate":"2020-06-25","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}"""
      val period2 = """{"startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}"""
      val bodyContents = s"""{"periods":[$period1,$period2]}"""
      val response = makePostRequest(getConnectionUrl("AS000400A"), bodyContents)
      response.status shouldBe Status.CREATED
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent without json body" in {
      val response = await(wsClient
        .url(getConnectionUrl("BS000400A"))
        .withHttpHeaders(Header.CorrelationId -> correlationHeaderValue.value.get)
        .post("")
      )
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent with good Nino but without json body" in {
      val response = await(wsClient
        .url(getConnectionUrl("AS000400A"))
        .withHttpHeaders(Header.CorrelationId -> correlationHeaderValue.value.get)
        .post("")
      )
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent with invalid json body" in {
      val response = makePostRequest(getConnectionUrl("BS000400A"), """{"notWhatWeAreExpecting":"certainlyNot"}""")
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 409(CONFLICT) when the Nino 'BS000409B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000409B"))
      response.status shouldBe Status.CONFLICT
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 428(PRECONDITION_REQUIRED) when the Nino 'BS000428B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000428B"))
      response.status shouldBe Status.PRECONDITION_REQUIRED
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS000500B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000500B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 502(BAD_GATEWAY) when the Nino 'BS000502B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000502B"))
      response.status shouldBe Status.BAD_GATEWAY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 503(SERVICE_UNAVAILABLE) when the Nino 'BS000503B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000503B"))
      response.status shouldBe Status.SERVICE_UNAVAILABLE
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }
  }

  "PUT /NINO/:nino/periods" should {
    "return 200(OK) with the periods sent when any accepted Nino value is sent" in {
      val periodId1 = """"periodID": "4043d4b5-1f2a-4d10-8878-ef1ce9d97b32""""
      val periodId2 = """"periodID": "6aed4f02-f652-4bef-af14-49c79e968c2e""""
      val period1 = s"""{$periodId1, "startDate":"2020-06-25","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}"""
      val period2 = s"""{$periodId2, "startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}"""
      val bodyContents = s"""{"periods":[$period1,$period2]}"""
      val response = makePutRequest(getConnectionUrl("AS000400A"), bodyContents)
      response.status shouldBe Status.OK
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent without json body" in {
      val response = await(wsClient
        .url(getConnectionUrl("BS000400A"))
        .withHttpHeaders(Header.CorrelationId -> correlationHeaderValue.value.get)
        .put("")
      )
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent with invalid json body" in {
      val response = makePutRequest(
        getConnectionUrl("BS000400A"),
        """{"notWhatWeAreExpecting":"certainlyNot"}"""
      )
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 409(CONFLICT) when the Nino 'BS000409B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000409B"))
      response.status shouldBe Status.CONFLICT
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 428(PRECONDITION_REQUIRED) when the Nino 'BS000428B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000428B"))
      response.status shouldBe Status.PRECONDITION_REQUIRED
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS000500B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000500B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 502(BAD_GATEWAY) when the Nino 'BS000502B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000502B"))
      response.status shouldBe Status.BAD_GATEWAY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 503(SERVICE_UNAVAILABLE) when the Nino 'BS000503B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000503B"))
      response.status shouldBe Status.SERVICE_UNAVAILABLE
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }
  }

  private def makePutRequest(connectionUrl: String, bodyContents: String = "{}")(implicit correlationId: CorrelationId) =
    await(wsClient.url(connectionUrl)
      .withHttpHeaders(Header.CorrelationId -> correlationId.value.get)
      .put(Json.parse(bodyContents)))

  private def makePostRequest(connectionUrl: String, bodyContents: String = "{}")(implicit correlationId: CorrelationId) =
    await(wsClient.url(connectionUrl)
      .withHttpHeaders(Header.CorrelationId -> correlationId.value.get)
      .post(Json.parse(bodyContents)))

  private def makeGetRequest(connectionUrl: String)(implicit correlationId: CorrelationId) =
    await(wsClient.url(connectionUrl)
      .withHttpHeaders(Header.CorrelationId -> correlationId.value.get)
      .get())

  private def getConnectionUrl(nino: String): String =
    s"${testServerAddress}/individuals/breathing-space/NINO/${nino}/periods"

  private def getExpectedResponseBody(filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/periods/$filename")
    Source.fromInputStream(in).getLines.mkString.replaceAll("\\s", "")
  }
}

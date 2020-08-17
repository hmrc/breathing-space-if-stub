package uk.gov.hmrc.breathingspace.controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.http.Status
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.breathingspace.config.AppConfig

class BreathingSpaceControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val fakeRequest = FakeRequest("GET", "/")

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration)
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  private val controller = new BreathingSpaceController(appConfig, Helpers.stubControllerComponents())

  "GET /" should {
    "return 200" in {
      val result = controller.retrieve()(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }
}

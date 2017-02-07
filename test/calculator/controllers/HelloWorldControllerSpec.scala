
package calculator.controllers

import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentType, _}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}


class HomeControllerSpec extends UnitSpec with WithFakeApplication{

  val fakeRequest = FakeRequest("GET", "/")


  "GET /" should {
    "return 200" in {
      val result = HomeController.welcome(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = HomeController.welcome(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }


  }


}

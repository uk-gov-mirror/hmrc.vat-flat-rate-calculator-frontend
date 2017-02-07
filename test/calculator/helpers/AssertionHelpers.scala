package calculator.helpers

import org.jsoup.select.Elements
import org.scalatest.Assertions.cancel

object AssertionHelpers {

  //requires a test to validate a non-empty array beffore using this assert
  def assertHtml(elements: Elements)(test: Elements => Unit): Unit = {
    if(elements.isEmpty) cancel("element not found")
    else test(elements)
  }
}

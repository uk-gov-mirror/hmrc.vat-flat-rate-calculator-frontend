package mocks

import calculator.config.AppConfig

class SimpleAppConfig extends AppConfig{
  override val analyticsToken: String = ""
  override val analyticsHost: String = ""
  override val reportAProblemNonJSUrl: String = ""
  override val reportAProblemPartialUrl: String = ""
}

package com.gatling.test

import io.gatling.core.Predef._
import io.gatling.core.feeder.Feeder
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

import scala.language.postfixOps
import scala.util.Random

class SeveralRpsAllAtOneWithMultipleUsers extends Simulation{

  val httpProtocol = http
    .baseUrl("https://mandaguari.qa.elotech.com.br")
    //.baseUrl("http://mandaguari.localhost:8089")//LOCAL
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .acceptHeader("text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
    .acceptEncodingHeader("gzip,deflate")
    .contentTypeHeader("text/xml;charset=UTF-8")
    .userAgentHeader("Apache-HttpClient/4.5.5 (Java/16.0.1)")

  val headers = Map("Proxy-Connection" -> "Keep-Alive")
  val r = new scala.util.Random

  private val refs = Iterator.continually(
    Map("reference" -> { Random.between(1000, 10000)}.toString )
  )

  val scn = scenario("UserPerMinuteSendingRps")
    .feed(refs)
    .exec(http("UserPerMinuteSendingRps")
      .post("/iss-ws/nfseService")
      .headers(headers)
      .body(ElFileBody("com/gatling/test/recordedsimulation/EnviarLoteRpsAssincronoEnvio.xml")))

  setUp(scn.inject(rampUsers(9).during(60))).protocols(httpProtocol)

}

package com.gatling.test

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

class SeveralRpsWithMoreThanOneInTheListOneByOneWithOneUser extends Simulation {

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
		Map("reference" -> { Random.between(1000, 99999)}.toString )
	)

	private val refsNota = Iterator.continually(
		Map("numnota1"-> { Random.between(1, 1000)}.toString,
			  "numnota2"-> { Random.between(1001, 2000)}.toString,
				"numnota3"-> { Random.between(2001, 3000)}.toString,
				"numnota4"-> { Random.between(3001, 4000)}.toString,
				"numnota5"-> { Random.between(4001, 5000)}.toString,
				"numnota6"-> { Random.between(5001, 6000)}.toString,
				"numnota7"-> { Random.between(6001, 7000)}.toString,
				"numnota8"-> { Random.between(7001, 8000)}.toString,
				"numnota9"-> { Random.between(8001, 9000)}.toString,
				"numnota10"-> { Random.between(9001, 10000)}.toString,
				"numnota11"-> { Random.between(10001, 11000)}.toString)
	)

	val scn = scenario("UserPerMinuteSendingRps")
		.repeat(300) {
				 feed(refs)
				.feed(refsNota)
				.exec(http("UserPerMinuteSendingRps")
				.post("/iss-ws/nfseService")
				.headers(headers)
				.body(ElFileBody("com/gatling/test/recordedsimulation/EnviarLoteRpsAssincronoEnvioVarioRPS.xml")))
		}

	setUp(scn.inject(atOnceUsers(9))).protocols(httpProtocol)
}
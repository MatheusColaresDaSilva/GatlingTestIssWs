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

	var numRPS = 11
	var valor = 1000
	private val refs = Iterator.continually({
		valor = valor + 1
		Map("reference" -> valor.toString,
			"numRps" -> numRPS.toString)
	}
	)

	var notas = scala.Predef.Map[String, String]()
	private val refsNota = Iterator.continually({
		for(i<-1.to(numRPS)) {
			valor= valor+1
			notas+= (s"numnota${i}" -> valor.toString)
		}
		notas
	}
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

	setUp(scn.inject(atOnceUsers(5))).protocols(httpProtocol)
}
package com.gatling.test

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ParallelTenantSeveralRpsWithMoreThanOneInTheListOneByOneWithOneUser extends Simulation {

	val httpProtocolMandaguari = http
		.baseUrl("https://mandaguari.qa.elotech.com.br")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
		.acceptEncodingHeader("gzip,deflate")
		.contentTypeHeader("text/xml;charset=UTF-8")
		.userAgentHeader("Apache-HttpClient/4.5.5 (Java/16.0.1)")

	val httpProtocolPontaGrossa = http
		.baseUrl("https://pontagrossa.qa.elotech.com.br")
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

	val scnMandaguari = scenario("MANDAGUARI_UserPerMinuteSendingRps")
		.repeat(2) {
				 feed(refs)
				.feed(refsNota)
				.exec(http("MANDAGUARI_UserPerMinuteSendingRps")
				.post("/iss-ws/nfseService")
				.headers(headers)
				.body(ElFileBody("com/gatling/test/recordedsimulation/EnviarLoteRpsAssincronoEnvioVarioRPS.xml")))
		}

	val scnPontaGrossa = scenario("PG_UserPerMinuteSendingRps")
		.repeat(600) {
			feed(refs)
				.feed(refsNota)
				.exec(http("PG_UserPerMinuteSendingRps")
					.post("/iss-ws/nfseService")
					.headers(headers)
					.body(ElFileBody("com/gatling/test/recordedsimulation/EnviarPGLoteRpsAssincronoEnvioVarioRPS.xml")))
		}

	setUp(scnMandaguari.inject(atOnceUsers(5)).protocols(httpProtocolMandaguari),scnPontaGrossa.inject(atOnceUsers(2)).protocols(httpProtocolPontaGrossa))
}
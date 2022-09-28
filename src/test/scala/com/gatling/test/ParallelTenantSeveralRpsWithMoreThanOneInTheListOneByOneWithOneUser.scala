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

	var valor = 1000
	private val refs = Iterator.continually({
		  valor = valor + 1
			Map("reference" -> valor.toString)
		}
	)

	private val refsNota = Iterator.continually({
		valor += 1
		val valor1 = valor
		valor += 1
		val valor2 = valor
		valor += 1
		val valor3 = valor
		valor += 1
		val valor4 = valor
		valor += 1
		val valor5 = valor
		valor += 1
		val valor6 = valor
		valor += 1
		val valor7 = valor
		valor += 1
		val valor8 = valor
		valor += 1
		val valor9 = valor
		valor += 1
		val valor10 = valor
		valor += 1
		val valor11 = valor

		Map("numnota1" -> valor1.toString,
			"numnota2" -> valor2.toString,
			"numnota3" -> valor3.toString,
			"numnota4" -> valor4.toString,
			"numnota5" -> valor5.toString,
			"numnota6" -> valor6.toString,
			"numnota7" -> valor7.toString,
			"numnota8" -> valor8.toString,
			"numnota9" -> valor9.toString,
			"numnota10" -> valor10.toString,
			"numnota11" -> valor11.toString)
	}
	)

	val scnMandaguari = scenario("MANDAGUARI_UserPerMinuteSendingRps")
		.repeat(300) {
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
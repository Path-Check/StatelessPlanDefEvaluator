package org.pathcheck.statelessplandefevaluator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONException
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.test.context.SpringBootTest
import java.io.InputStream

@SpringBootTest
class WebApplicationTests {
	private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
	private val jsonParser = fhirContext.newJsonParser()

	fun parse(assetName: String): IBaseResource {
		return jsonParser.parseResource(open(assetName))
	}

	fun open(assetName: String): InputStream {
		return javaClass.getResourceAsStream(assetName)!!
	}

	fun load(asset: InputStream): String {
		return asset.bufferedReader().use { bufferReader -> bufferReader.readText() }
	}

	fun load(assetName: String): String {
		return load(open(assetName))
	}

	@Test
	fun contextLoads() {
	}

	@Test
	fun testSimpleDataIn() {
		val op = PlanDefinitionOperator(parse("/SimpleDataIn.json") as Bundle)
		val carePlan = op.apply("Test-PlanDefinitionCondition", "Test-Patient", "Test-Encounter")

		try {
			JSONAssert.assertEquals(
				load("/SimpleDataOut.json"),
				jsonParser.encodeResourceToString(carePlan),
				true
			)
		} catch (e: JSONException) {
			e.printStackTrace()
		} catch (e: AssertionError) {
			println("Actual: " + jsonParser.encodeResourceToString(carePlan))
			throw e
		}
	}

}

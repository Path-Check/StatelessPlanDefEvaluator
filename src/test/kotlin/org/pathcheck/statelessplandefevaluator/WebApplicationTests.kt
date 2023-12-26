package org.pathcheck.statelessplandefevaluator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import io.mockk.every
import io.mockk.mockkStatic
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONException
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.test.context.SpringBootTest
import java.io.InputStream
import java.util.*

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
		val uuidSource = listOf(
			UUID.fromString("1acd6ee8-7459-40c4-b16e-0b54a6f5794a"),
			UUID.fromString("2708db33-a235-49cc-9092-ec2e47572401"),
			UUID.fromString("3acd6ee8-7459-40c4-b16e-0b54a6f5794a"),
			UUID.fromString("4acd6ee8-7459-40c4-b16e-0b54a6f5794a"),
			UUID.fromString("5708db33-a235-49cc-9092-ec2e47572401"),
			UUID.fromString("6acd6ee8-7459-40c4-b16e-0b54a6f5794a"),
			UUID.fromString("7acd6ee8-7459-40c4-b16e-0b54a6f5794a"),
			UUID.fromString("8708db33-a235-49cc-9092-ec2e47572401"),
			UUID.fromString("9acd6ee8-7459-40c4-b16e-0b54a6f5794a"),
		)

		mockkStatic(UUID::class)
		every { UUID.randomUUID() } returnsMany uuidSource

		val op = PlanDefinitionOperator(parse("/SimpleDataIn.json") as Bundle)
		val carePlan = op.apply(
			"http://localhost/PlanDefinition/Test-PlanDefinitionCondition-1.0.0",
			"Patient/Test-Patient",
			"Test-Encounter"
		)

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

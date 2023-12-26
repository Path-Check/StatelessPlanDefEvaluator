package org.pathcheck.statelessplandefevaluator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CarePlan
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
open class JacksonConfig {
    @Bean
    open fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer? {
        return Jackson2ObjectMapperBuilderCustomizer {
            builder: Jackson2ObjectMapperBuilder ->
                builder
                    .serializers(
                        FhirSerializer(CarePlan::class.java)
                    )
                    .deserializers(
                        FhirDeserializer(Bundle::class.java)
                    )
        }
    }

    class FhirSerializer<K : IBaseResource>(private val myClazz: Class<K>) : JsonSerializer<K>() {
        private val jsonParser: IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

        override fun serialize(dt: K, json: JsonGenerator, prov: SerializerProvider?) {
            json.enable(JsonGenerator.Feature.IGNORE_UNKNOWN)
            json.writeRaw(jsonParser.encodeResourceToString(dt))
        }

        override fun handledType(): Class<K> {
            return myClazz
        }
    }

    class FhirDeserializer<K : IBaseResource>(private val myClazz: Class<K>) : JsonDeserializer<K>() {
        private val jsonParser: IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): K? {
            if (p != null) {
                return jsonParser.parseResource(p.codec.readTree<JsonNode>(p).toString()) as K
            }
            return null
        }

        override fun handledType(): Class<K> {
            return myClazz
        }
    }
}
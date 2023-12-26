package org.pathcheck.statelessplandefevaluator

import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {

    data class PlanDefEvaluate(
        val data: Bundle,
        val planDefinitionUrl: String,
        val subject: String,
        val encounterID: String
    )

    @PostMapping("/evaluate")
    fun evaluate(@RequestBody input: PlanDefEvaluate): IBaseResource? {

        println(input.planDefinitionUrl)
        println(input.subject)
        println(input.encounterID)

        return PlanDefinitionOperator(input.data).apply(
            input.planDefinitionUrl,
            input.subject,
            input.encounterID
        )
    }

}
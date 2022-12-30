package org.pathcheck.statelessplandefevaluator

import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {

    data class PlanDefEvaluate(
        val data: Bundle,
        val planDefinitionID: String,
        val patientID: String,
        val encounterID: String
    )

    @PostMapping("/evaluate")
    fun verify(@RequestBody input: PlanDefEvaluate): IBaseResource? {
        return PlanDefinitionOperator(input.data).apply(
            input.planDefinitionID,
            input.patientID,
            input.encounterID
        )
    }

}
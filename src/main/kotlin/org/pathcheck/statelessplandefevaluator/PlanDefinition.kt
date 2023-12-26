package org.pathcheck.statelessplandefevaluator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.CanonicalType
import org.opencds.cqf.fhir.cql.EvaluationSettings
import org.opencds.cqf.fhir.cql.LibraryEngine
import org.opencds.cqf.fhir.cr.plandefinition.r4.PlanDefinitionProcessor

class PlanDefinitionOperator(private val data: IBaseBundle) {
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val evaluationSettings: EvaluationSettings = EvaluationSettings.getDefault()

  fun apply(planDefinitionUrl: String, subject: String, encounterId: String? = null): IBaseResource? {
    val repository = InMemoryBundleRepository(fhirContext)
    repository.addAll(data)

    val libraryProcessor = LibraryEngine(repository, evaluationSettings)
    val planDefProcessor = PlanDefinitionProcessor(repository, evaluationSettings)

    return planDefProcessor
      .apply(
        /* planDefinitionID */ null,
        CanonicalType(planDefinitionUrl),
        /* planDefinition = */ null,
        /* subject = */ subject,
        /* encounterId = */ encounterId,
        /* practitionerId = */ null,
        /* organizationId = */ null,
        /* userType = */ null,
        /* userLanguage = */ null,
        /* userTaskContext = */ null,
        /* setting = */ null,
        /* settingContext = */ null,
        /* parameters = */ null,
        /* useServerData = */ null,
        /* bundle = */ null,
        /* prefetchData = */ null,
        libraryProcessor
      )
  }
}

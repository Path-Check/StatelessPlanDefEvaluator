package org.pathcheck.statelessplandefevaluator

import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.opencds.cqf.fhir.utility.repository.InMemoryFhirRepository

class InMemoryBundleRepository(fhirContext: FhirContext) : InMemoryFhirRepository(fhirContext) {
  fun addAll(resource: IBaseResource) {
    when (resource) {
      is Bundle -> resource.entry.forEach { addAll(it.resource) }
      else -> create(resource)
    }
  }
}

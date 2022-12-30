package org.pathcheck.statelessplandefevaluator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Endpoint
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Parameters
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverterFactory
import org.opencds.cqf.cql.evaluator.activitydefinition.r4.ActivityDefinitionProcessor
import org.opencds.cqf.cql.evaluator.builder.Constants
import org.opencds.cqf.cql.evaluator.builder.CqlEvaluatorBuilder
import org.opencds.cqf.cql.evaluator.builder.EndpointConverter
import org.opencds.cqf.cql.evaluator.builder.data.DataProviderFactory
import org.opencds.cqf.cql.evaluator.builder.data.FhirModelResolverFactory
import org.opencds.cqf.cql.evaluator.builder.data.TypedRetrieveProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.LibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.TypedLibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TypedTerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.cql2elm.content.fhir.BundleFhirLibrarySourceProvider
import org.opencds.cqf.cql.evaluator.cql2elm.util.LibraryVersionSelector
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider
import org.opencds.cqf.cql.evaluator.engine.terminology.BundleTerminologyProvider
import org.opencds.cqf.cql.evaluator.expression.ExpressionEvaluator
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal
import org.opencds.cqf.cql.evaluator.library.CqlFhirParametersConverter
import org.opencds.cqf.cql.evaluator.library.LibraryProcessor
import org.opencds.cqf.cql.evaluator.plandefinition.OperationParametersParser
import org.opencds.cqf.cql.evaluator.plandefinition.r4.PlanDefinitionProcessor

class PlanDefinitionOperator(val data: IBaseBundle) {
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)

  fun buildProcessor(fhirDal: FhirDal): PlanDefinitionProcessor {
    val adapterFactory = AdapterFactory()
    val libraryVersionSelector = LibraryVersionSelector(adapterFactory)
    val fhirTypeConverter = FhirTypeConverterFactory().create(fhirContext.version.version)
    val cqlFhirParametersConverter = CqlFhirParametersConverter(fhirContext, adapterFactory, fhirTypeConverter)

    val fhirModelResolverFactory = FhirModelResolverFactory()

    val librarySourceProviderFactories =
      setOf<TypedLibrarySourceProviderFactory>(
        object : TypedLibrarySourceProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String, headers: List<String>?) =
            BundleFhirLibrarySourceProvider(
              fhirContext,
              data,
              adapterFactory,
              libraryVersionSelector
            )
        }
      )

    val librarySourceProviderFactory =
      LibrarySourceProviderFactory(
        fhirContext,
        adapterFactory,
        librarySourceProviderFactories,
        libraryVersionSelector
      )

    val retrieveProviderFactories =
      setOf<TypedRetrieveProviderFactory>(
        object : TypedRetrieveProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String, headers: List<String>?) = BundleRetrieveProvider(fhirContext, data)
        }
      )

    val dataProviderFactory =
      DataProviderFactory(fhirContext, setOf(fhirModelResolverFactory), retrieveProviderFactories)

    val typedTerminologyProviderFactories =
      setOf<TypedTerminologyProviderFactory>(
        object : TypedTerminologyProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String, headers: List<String>?) = BundleTerminologyProvider(fhirContext, data)
        }
      )

    val terminologyProviderFactory = TerminologyProviderFactory(fhirContext, typedTerminologyProviderFactories)

    val endpointConverter = EndpointConverter(adapterFactory)

    val libraryProcessor =
      LibraryProcessor(
        fhirContext,
        cqlFhirParametersConverter,
        librarySourceProviderFactory,
        dataProviderFactory,
        terminologyProviderFactory,
        endpointConverter,
        fhirModelResolverFactory
      ) { CqlEvaluatorBuilder() }

    val evaluator =
      ExpressionEvaluator(
        fhirContext,
        cqlFhirParametersConverter,
        librarySourceProviderFactory,
        dataProviderFactory,
        terminologyProviderFactory,
        endpointConverter,
        fhirModelResolverFactory
      ) { CqlEvaluatorBuilder() }

    val activityDefProcessor = ActivityDefinitionProcessor(fhirContext, fhirDal, libraryProcessor)
    val operationParametersParser = OperationParametersParser(adapterFactory, fhirTypeConverter)

    return PlanDefinitionProcessor(
      fhirContext,
      fhirDal,
      libraryProcessor,
      evaluator,
      activityDefProcessor,
      operationParametersParser
    )
  }

  fun apply(planDefinitionID: String, patientID: String, encounterID: String?): IBaseResource? {
    val dataEndpoint =
      Endpoint()
        .setAddress("localPost")
        .setConnectionType(Coding().setCode(Constants.HL7_FHIR_FILES))

    val fhirDal = InMemoryFhirDal()
    fhirDal.addAll(data)

    return buildProcessor(fhirDal)
      .apply(
        IdType("PlanDefinition", planDefinitionID),
        patientID,
        encounterID,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        Parameters(),
        null,
        data as Bundle,
        null,
        dataEndpoint,
        dataEndpoint,
        dataEndpoint
      )
  }
}

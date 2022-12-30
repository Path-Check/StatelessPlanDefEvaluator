# StatelessPlanDefEvaluator

This is a FHIR PlanDefinition Evaluator as a stateless webservice. Every call requires users to send the full dataset for evaluation. Nothing is saved.  

# Running

This is a Spring boot project. Run with: 

```
./gradlew bootRun
```

# Usage

The server listens to a POST in `/evaluate` with the following payload:

```json
{
  "planDefinitionID": "Test-PlanDefinitionCondition",
  "patientID": "Test-Patient",
  "encounterID": "Test-Encounter", 
  "data": {
    // JSON BUNDLE 
  }
}
```

and outputs a CarePlan

```json
{
  "resourceType": "CarePlan"
  // ...
}
```

Check `SimpleData.http` for a runnable post example.  


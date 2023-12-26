# StatelessPlanDefEvaluator

This is a FHIR PlanDefinition Evaluator as a stateless webservice. Every call requires users to send the full dataset for evaluation. Nothing is saved.  

# Live Instance

Visit [plan-def-evaluator.pathcheck.org](https://plan-def-evaluator.pathcheck.org/) to see the latest version

# Running

This is a Spring boot project. Run with: 

```
./gradlew bootRun
```

# Usage

The server listens to a POST in `/evaluate` with the following payload:

```json
{
  "planDefinitionUrl": "http://localhost/PlanDefinition/Test-PlanDefinitionCondition-1.0.0",
  "subject": "Patient/Test-Patient",
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


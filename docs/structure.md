The Impact Analysis repo contains two major bundle categories:

## Impact Analysis 
The impact analysis itself is contained in the only Eclipse bundle of the project in the `bundles/dev.abunai.impact.analysis/` folder.
It contains the full sources of the analysis. 
The bundle contains the following packages:
- `dev.abunai.impact.analysis`: The main package of the analysis containing the high-level analysis objects and the CLI
- `dev.abunai.impact.analysis.interactive`: Functionality regarding the CLI and Arc3n integration 
- `dev.abunai.impact.analysis.model`: Contains the `UncertaintySourceCollection` and `UncertaintyImpactCollection` as well as the different possible impact and source types
- `dev.abunai.impact.analysis.util`: Utility classes for working with Palladio Component Model (PCM) models

## Impact Analysis Tests
### Tests
The tests for the impact analysis care contained in the `tests/dev.abunai.impact.analysis.tests` Eclipse project.
The two Functionality tests are contained in the `dev.abunai.impact.analysis.tests` package:
The `AnalysisTest.java` file tests the general functionality of the analysis using the `InternationalOnlineShop` model. 
The `BranchingTest.java` file tests the functionality of the analysis using `Branch` PCM elements in the `BranchingOnlineShop` testmodel. 

The `ImpactAnnotator.java` class contains functionality to create models for the web editor of the data flow analysis for the visualization of the impact of uncertainties and the contained violations against the constraint of the model. 
The `TestBase.java` class encapsulates common functionality required for all different evaluation scenarios contained in the `dev.abunai.impact.analysis.test.evaluation` package. 

In the `EvaluationBase.java` class the four different tests for each evaluation scenario are defined: 
- `evaluateScenario()`: Runs the impact analysis and outputs the violations against the given constraint
- `storeAnnotatedResult()`: Stores an annotated Dataflow Diagram for the impact and violations of the model using the `ImpactAnnotator`
- `printAllDataFlows()`: Prints all data flows that are found by the analysis
- `printMetrics()`: Prints all metrics used in the evaluation of the impact analysis

For the `CoronaWarnApp` evaluation scenarios, refer to the `dev.abunai.impact.analysis.tests.evaluation.cwa` package.
For the `MobilityAsAService` evaluation scenarios, refer to the `dev.abunai.impact.analysis.tests.evaluation.maas` package.

### Testmodels
The corresponding testmodels can be found in `tests/dev.abunai.impact.analysis.testmodels/`.
The activator for the modelling project can be found in the `src/` directory.
No other sources are provided by the testmodels project. 
The `models/` directory contains the two models used in testing the Functionality of the impact analysis. 
Both the `BranchingOnlineShop` and `InternationalOnlineShop` are variations on the same testmodel. 
The `BranchingOnlineShop` uses `Branch` PCM elements, whereas the `InternationalOnlineShop` uses different deployments.



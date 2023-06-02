# Uncertainty Impact Analysis

The **Uncertainty Impact Analysis** calculates impact sets of uncertainty sources.
This is achieved by internally extracting all data flows and propagating uncertainty through [Palladio](https://www.palladio-simulator.com/) software architecture models and along those data flows.

More information can be found in this publication: S. Hahner, R. Heinrich, and R. Reussner, "Architecture based Uncertainty Impact Analysis to ensure Confidentiality", in *18th Symposium on Software Engineering for Adaptive and Self-Managing Systems (SEAMS)*, IEEE/ACM, 2023, accepted, to appear.

> *Warning:* Please be aware that this implementation only represents an early prototype. Brace yourself for bad internal quality and code smells :)

## Installation

1. Install the version `2022-12` of the Eclipse Modelling Tools from the [official site](https://www.eclipse.org/downloads/packages/release/2022-12/r/eclipse-modeling-tools)
2. Clone the confidentiality analysis repository from [GitHub](https://github.com/PalladioSimulator/Palladio-Addons-DataFlowConfidentiality-Analysis).
3. Import the dependencies.p2f file into Eclipse to install the dependencies of the project. This is achieved by going to File->Import->General->Install from File
4. Import all projects of the data flow analysis and also all projects from the `bundles` and `tests` folders from this repository
5. **Optional**: If you wish to run the analysis on the [Corona Warn App case study](https://github.com/abunai-dev/CaseStudy-CoronaWarnApp), also clone that repository into the case studies folder. The structure should look like this: `tests/dev.abunai.impact.analysis.testmodels/casestudies/CaseStudy-CoronaWarnApp`

## Usage

- The bundle `dev.abunai.impact.analysis.tests` contains several test cases to test the functionality and to run the evaluation
- The bundle `dev.abunai.impact.analysis.testmodels` contains models for testing and evaluation
- The tests demonstrate how to use the analysis to annotate and to propagate uncertainty
- The test classes in the `evaluation` package can be used to execute the evaluation, see the optional installation step above

## More information

For more information, please refer to [https://abunai.dev](https://abunai.dev).

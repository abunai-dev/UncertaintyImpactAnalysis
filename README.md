# Uncertainty Impact Analysis

The **Uncertainty Impact Analysis** calculates impact sets of uncertainty sources.
This is achieved by internally extracting all data flows and propagating uncertainty through [Palladio](https://www.palladio-simulator.com/) software architecture models and along those data flows.

More information can be found in this publication: S. Hahner, R. Heinrich, and R. Reussner, "Architecture-Based Uncertainty Impact Analysis to Ensure Confidentiality", in *18th Symposium on Software Engineering for Adaptive and Self-Managing Systems (SEAMS)*, IEEE/ACM, 2023, doi: [10.1109/SEAMS59076.2023.00026](https://doi.org/10.1109/SEAMS59076.2023.00026)

> [!WARNING]
> Please be aware that this implementation only represents an early prototype. Brace yourself for bad internal quality and code smells :)

## Installation

1. Download the [data flow analysis](https://github.com/DataFlowAnalysis) product from [product.dataflowanalysis.org](https://updatesite.palladio-simulator.com/DataFlowAnalysis/product/releases/1.0.0/).
2. Extract all files from the archive, launch the `eclipse.exe` and choose a folder as workspace (or just take the default one).
3. Clone this repository into the workspace.
4. Import all projects of the data flow analysis and also all projects from the `bundles` and `tests` folders from this repository using *File->Import->General->Existing Projects into Workspace*. This should import **3** projects in total.
5. Execute the tests cases located in `dev.abunai.impact.analysis.tests` to make sure everything is working correctly.
6. **Optional**: If you wish to run the analysis on the [Corona Warn App case study](https://github.com/abunai-dev/CaseStudy-CoronaWarnApp), also clone that repository into the case studies folder. The structure should look like this: `tests/dev.abunai.impact.analysis.testmodels/casestudies/CaseStudy-CoronaWarnApp`. Make sure to checkout the `uncertainty-impact-scenarios` branch before executing the evaluation test cases.

## Usage

- The bundle `dev.abunai.impact.analysis.tests` contains several test cases to test the functionality and to run the evaluation
- The bundle `dev.abunai.impact.analysis.testmodels` contains models for testing and evaluation
- The tests demonstrate how to use the analysis to annotate and to propagate uncertainty
- The test classes in the `evaluation` package can be used to execute the evaluation, see the optional installation step above

## More information

For more information, please refer to [https://abunai.dev](https://abunai.dev).

<p align="center"> 
    <h3 align="center"><img alt="ABUNAI" src="abunai-art-240.png"><br>
    UIA</h3>
</p>
<p>&nbsp;</p>

## UIA: Uncertainty Impact Analysis

[![xDECAF analysis framework](https://img.shields.io/badge/xDECAF%20analysis%20framework-v4.0.0-orange?style=flat-square&logo=eclipse&logoColor=white)](https://dataflowanalysis.org)
[![Dissertation](https://img.shields.io/badge/Dissertation-Available-green?style=flat-square&logo=GitBook&logoColor=white)](https://doi.org/10.5445/IR/1000178700)
[![Overview Slides](https://img.shields.io/badge/Overview%20Slides-Available-green?style=flat-square&logo=Slides&logoColor=white)](https://sebastianhahner.de/talks/2024/DoctoralDefenseSebastianHahner_2024_ArchitectureBasedAndUncertaintyAwareConfidentialityAnalysis.pdf)

UIA is part of the ABUNAI approach to uncertainty-aware confidentiality analysis.
This repository provides tool support for the third step of the procedure shown below: The propagation of uncertainty sources.
For further information, please see the [dissertation](https://doi.org/10.5445/IR/1000178700) *Chapter 4.1* on the procedure and *Chapter 6* on the uncertainty propagation to enable
uncertainty impact analysis.
<p>&nbsp;</p>

![Procedure](uia-light.png#gh-light-mode-only)
![Procedure](uia-dark.png#gh-dark-mode-only)

## Overview

The **Uncertainty Impact Analysis** calculates impact sets of uncertainty sources.
This is achieved by internally extracting all data flows and propagating uncertainty through [Palladio](https://www.palladio-simulator.com/) software architecture models and along those data flows.

The analysis has been initially released with this publication: S. Hahner, R. Heinrich, and R. Reussner, "Architecture-Based Uncertainty Impact Analysis to Ensure Confidentiality", in *18th Symposium on Software Engineering for Adaptive and Self-Managing Systems (SEAMS)*, IEEE/ACM, 2023, doi: [10.1109/SEAMS59076.2023.00026](https://doi.org/10.1109/SEAMS59076.2023.00026)

## Installation

1. Download the [data flow analysis](https://github.com/DataFlowAnalysis) product from [product.dataflowanalysis.org](https://updatesite.palladio-simulator.com/DataFlowAnalysis/product/releases/4.0.0/).
2. Extract all files from the archive, launch the `eclipse.exe` and choose a folder as workspace (or just take the default one).
3. Clone this repository into the workspace.
4. Import all projects of the data flow analysis and also all projects from the `bundles` and `tests` folders from this repository using *File->Import->General->Existing Projects into Workspace*. This should import **3** projects in total.
5. Execute the tests cases located in `dev.abunai.impact.analysis.tests` to make sure everything is working correctly.
6. **Optional**: If you wish to run the analysis evaluation, additionally clone the evaluation scenario repositories from the organization. Further information can be found in the [dissertation](https://doi.org/10.5445/IR/1000178700).

## Usage

- The bundle `dev.abunai.impact.analysis.tests` contains several test cases to test the functionality and to run the evaluation
- The bundle `dev.abunai.impact.analysis.testmodels` contains models for testing and evaluation
- The tests demonstrate how to use the analysis to annotate and to propagate uncertainty

There are multiple ways to use the analysis, see the further documentation in the `docs` folder.
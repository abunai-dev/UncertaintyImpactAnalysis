## Data Objects 
### Uncertainty Sources 
A **Uncertainty Source** affects an element *in the model* with an Uncertainty.
It may be of the following types:
- Actor: Uncertainty affecting a `UsageScenario` or `ResourceContainer`
- Behavior: Uncertainty affecting a `EntryLevelSystemCall`, `ExternalCallAction`, `SetVariableAction` or `StartAction`
- Component: Uncertainty affecting a `AssemblyContext`
- Connector: Uncertainty affecting a `AssemblyConnector` or `ProvidedDelegationConnector`
- Interface: Uncertainty affecting a `OperationSignature`
The Evaluation of one **Uncertainty Source** may result in one or multiple **Uncertainty Impacts**

### Uncertainty Impact 
A **Uncertainty Impact** affects an element *in the transpose flow graph* and is a result of an **Uncertainty Source**.
They result from the Evaluation of the different **Uncertainty Source** types: 
- Actor: Vertices that match the affected `UsageScenario` or `ResourceContainer`
- Behavior: Vertices that match the affected `EntryLevelSystemCall`, `ExternalCallAction`, `SetVariableAction` or `StartAction`
- Component: Vertices that match the affected `AssemblyContext`
- Connector: Vertices that match the affected `AssemblyConnector` or `ProvidedDelegationConnector`
- Interface: Vertices that match the affected `OperationSignature`

### Uncertainty Source Collection 
A **Uncertainty Source Collection** contains multiple **Uncertainty Sources** that affect a contained collection of Transpose Flow Graphs. 
Uncertainties affecting the Transpose Flow Graphs are added to elements of this type.
The **Uncertainty Source Collection** can be evaluated into an **Uncertainty Impact Collection** to determine the impact of the **Uncertainty Sources**

### Uncertainty Impact Collection 
The **Uncertainty Impact Collection** contains the **Uncertainty Impact** elements for a collection of Transpose Flow Graphs.
It originates from an **Uncertainty Source Collection**.
This object can be used to determine the **Impact Set** of the given **Uncertainty Sources**

### Impact Set 
The **Impact Set** is a collection of (partial) Transpose Flow Graphs that represent the impact of **Uncertainty Sources**.
For each original Transpose Flow Graph, it represents the partial Transpose Flow Graph following the impacted vertices by the **Uncertainty Sources** and corresponding **Uncertainty Impacts**.

A **Distinct Impact Set** determines for each original Transpose Flow Graph the largest partial Transpose Flow Graph that is contained in the Impact Set.

### Impact Annotator 
The **Impact Annotator** is responsible for creating an annotated representation of the **Impact Set**.
It first converts the input model to a Data Flow Diagram, then uses the **Impact Set** and constraint violations to annotate the corresponding elements in the Data Flow Diagram. 
It can be found in the `dev.abunai.impact.analysis.tests` bundle in the `dev.abunai.impact.analysis.tests` package 

## Analysis Workflow
The workflow for the impact analysis should follow the following steps:
1. Create an analysis using the `PCMUncertaintyImpactAnalysisBuilder`
2. Add **Uncertainty Sources** to elements using the analysis and `add<X>UncertaintySource()` where `<X>` is the type of the **Uncertainty Source**
3. Evaluate the impacts of the **Uncertainty Sources** with `propagate()`
4. Use the resulting `UncertaintyImpactCollection` to determine the **Impact Set** with `getImpactSet(distinct)`
5. Optionally, run the normal Confidentiality Analysis with `findFlowGraphs()` and call `evaluate()` on the resulting `FlowGraphCollection`
6. Optionally, create an annotated Data Flow Diagram with the `ImpactAnnotator`

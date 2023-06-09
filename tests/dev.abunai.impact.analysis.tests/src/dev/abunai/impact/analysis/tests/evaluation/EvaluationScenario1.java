package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class EvaluationScenario1 extends EvaluationBase {

	@Override
	protected String getFolderName() {
		return "CoronaWarnApp_UncertaintyScenario1";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Override
	String getScenarioName() {
		return "Scenario 1";
	}

	@Override
	void addUncertaintySources() {
		// Scenario 1: "One component still uncertain, the others not"
		analysis.addConnectorUncertaintyInConnector("_w-qoYLNzEe2o46d27a6tVQ"); // S1_1
		analysis.addActorUncertaintyInResourceContainer("_E9SLkLN3Ee2o46d27a6tVQ"); // S1_2
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// S1_1
			if (dataLiterals.contains("ConnectionIntercepted")) {
				return true;
			}

			// S1_2
			if (nodeLiterals.contains("IllegalDeploymentLocation")) {
				return true;
			}

			return false;
		};
	}

}

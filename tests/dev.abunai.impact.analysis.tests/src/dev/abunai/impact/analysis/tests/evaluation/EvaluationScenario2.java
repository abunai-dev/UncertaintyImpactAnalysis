package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class EvaluationScenario2 extends EvaluationBase {

	@Override
	protected String getFolderName() {
		return "CoronaWarnApp_UncertaintyScenario2";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Override
	String getScenarioName() {
		return "Scenario 2";
	}

	@Override
	void addUncertaintySources() {
		// Scenario 2: "Environmental uncertainty in the deployment and system context"
		analysis.addActorUncertaintyInResourceContainer("_wqni4MP5Ee2NifGpaUwYsQ"); // S2_1
		analysis.addBehaviorUncertaintyInExternalCallAction("_YIPkQLm8Ee2dIMSi7oNVYQ"); // S2_2
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// S2_1
			if (nodeLiterals.contains("IllegalDeploymentLocation")) {
				return true;
			}

			// S2_2
			if (dataLiterals.contains("ConfidentialDataNotExpected")) {
				return true;
			}

			return false;
		};
	}

}

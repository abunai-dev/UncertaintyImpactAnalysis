package dev.abunai.impact.analysis.tests.evaluation.cwa;

import java.util.List;
import java.util.function.BiPredicate;

public class CWAEvaluationScenario2Test extends CWAEvaluationBase {

	@Override
	protected String getFolderName() {
		return "CoronaWarnApp_UncertaintyScenario2";
	}

	@Override
	protected String getScenarioName() {
		return "Scenario 2";
	}

	@Override
	protected void addUncertaintySources() {
		// Scenario 2: "Environmental uncertainty in the deployment and system context"
		analysis.getUncertaintySources().addActorUncertaintyInResourceContainer("_wqni4MP5Ee2NifGpaUwYsQ"); // S2_1
		analysis.getUncertaintySources().addBehaviorUncertaintyInExternalCallAction("_YIPkQLm8Ee2dIMSi7oNVYQ"); // S2_2
	}

	@Override
	protected BiPredicate<List<String>, List<String>> getConstraint() {
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

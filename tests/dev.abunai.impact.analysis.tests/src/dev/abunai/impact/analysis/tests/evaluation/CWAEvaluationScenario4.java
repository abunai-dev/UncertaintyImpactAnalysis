package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class CWAEvaluationScenario4 extends EvaluationBase {

	@Override
	protected String getFolderName() {
		return "CoronaWarnApp_UncertaintyScenario4";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Override
	String getScenarioName() {
		return "Scenario 4";
	}

	@Override
	void addUncertaintySources() {
		// Scenario 4: "Critical points in the system with wide impact"
		analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext("_v1LV8LNhEe2o46d27a6tVQ"); // S4_1
		analysis.getUncertaintySources().addInterfaceUncertaintyInInterface("_FC4gkLHzEe2fRLFFhL_FWA"); // S4_2
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// S4_1
			if (dataLiterals.contains("KeyIssue")) {
				return true;
			}

			// S4_2
			if (dataLiterals.contains("RetrievedConfidentialDetails")) {
				return true;
			}

			return false;
		};
	}

}

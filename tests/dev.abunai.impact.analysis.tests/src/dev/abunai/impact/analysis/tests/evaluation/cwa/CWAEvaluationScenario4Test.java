package dev.abunai.impact.analysis.tests.evaluation.cwa;

import java.util.List;
import java.util.function.BiPredicate;

public class CWAEvaluationScenario4Test extends CWAEvaluationBase {

	@Override
	protected String getFolderName() {
		return "CoronaWarnApp_UncertaintyScenario4";
	}

	@Override
	protected String getScenarioName() {
		return "Scenario 4";
	}

	@Override
	protected void addUncertaintySources() {
		// Scenario 4: "Critical points in the system with wide impact"
		analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext("_v1LV8LNhEe2o46d27a6tVQ"); // S4_1
		analysis.getUncertaintySources().addInterfaceUncertaintyInInterface("_FC4gkLHzEe2fRLFFhL_FWA"); // S4_2
	}

	@Override
	protected BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// S4_1
			if (dataLiterals.contains("KeyIssue")) {
				return true;
			}

			// S4_2
            return dataLiterals.contains("RetrievedConfidentialDetails");
        };
	}

}

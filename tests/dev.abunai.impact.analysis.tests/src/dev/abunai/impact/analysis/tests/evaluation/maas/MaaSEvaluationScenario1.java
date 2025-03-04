package dev.abunai.impact.analysis.tests.evaluation.maas;

import java.util.List;
import java.util.function.BiPredicate;

public class MaaSEvaluationScenario1 extends MaaSEvaluationBase {
	@Override
	protected String getFolderName() {
		return "MaaS_UncertaintyScenario1";
	}

	@Override
	protected String getScenarioName() {
		return "Scenario 1";
	}

	@Override
	protected void addUncertaintySources() {
		// Scenario 1: "One component still uncertain, the others not"
		analysis.getUncertaintySources().addActorUncertaintyInUsageScenario("_LU9TwBzVEe-jDKmmiRikig"); // U1
	}

	@Override
	protected BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// U1
			if (nodeLiterals.contains("Customer") && dataLiterals.contains("LoginData")) {
				return true;
			}
			if (dataLiterals.contains("Leaked")) {
				return true;
			}
			if (nodeLiterals.contains("MaliciousActor")) {
				return true;
			}

			return false;
		};
	}

}

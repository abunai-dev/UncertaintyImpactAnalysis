package dev.abunai.impact.analysis.tests.evaluation.maas;

import java.util.List;
import java.util.function.BiPredicate;

public class MaaSEvaluationScenario5Test extends MaaSEvaluationBase {
	@Override
	protected String getFolderName() {
		return "MaaS_UncertaintyScenario5";
	}

	@Override
	protected String getScenarioName() {
		return "Scenario 5";
	}

	@Override
	protected void addUncertaintySources() {
		// Scenario 1: "One component still uncertain, the others not"
		analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext("_aUhbIFixEe-1dYFuXzLZMA"); // U5
	}

	@Override
	protected BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// U5
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

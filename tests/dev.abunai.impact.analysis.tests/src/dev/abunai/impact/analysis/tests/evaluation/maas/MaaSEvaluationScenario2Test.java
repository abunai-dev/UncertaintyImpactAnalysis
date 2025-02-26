package dev.abunai.impact.analysis.tests.evaluation.maas;

import java.util.List;
import java.util.function.BiPredicate;

public class MaaSEvaluationScenario2Test extends MaaSEvaluationBase {
	@Override
	protected String getFolderName() {
		return "MaaS_UncertaintyScenario2";
	}

	@Override
	protected String getScenarioName() {
		return "Scenario 2";
	}

	@Override
	protected void addUncertaintySources() {
		analysis.getUncertaintySources().addBehaviorUncertaintyInSetVariableAction("_y87REFitEe-1dYFuXzLZMA"); // U2
	}

	@Override
	protected BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// U2
			if (nodeLiterals.contains("Customer") && dataLiterals.contains("LoginData")) {
				return true;
			}
			if (dataLiterals.contains("Leaked")) {
				return true;
			}
            return nodeLiterals.contains("MaliciousActor");
        };
	}
}

package dev.abunai.impact.analysis.tests.evaluation.maas;

import java.util.List;
import java.util.function.BiPredicate;

public class MaaSEvaluationScenario3Test extends MaaSEvaluationBase {
	@Override
	protected String getFolderName() {
		return "MaaS_UncertaintyScenario3";
	}
	
	@Override
	protected String getScenarioName() {
		return "Scenario 3";
	}

	@Override
	protected void addUncertaintySources() {
		analysis.getUncertaintySources().addInterfaceUncertaintyInSignature("_dVFMoFiyEe-1dYFuXzLZMA"); // U3
	}

	@Override
	protected BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// U3
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

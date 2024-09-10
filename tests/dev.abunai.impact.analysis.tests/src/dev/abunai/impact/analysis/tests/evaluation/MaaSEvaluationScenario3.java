package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class MaaSEvaluationScenario3  extends EvaluationBase {
	
	@Override
	protected String getBaseFolder() {
		return "casestudies";
	}


	@Override
	protected String getFolderName() {
		return "MaaS_UncertaintyScenario3";
	}

	@Override
	protected String getFilesName() {
		return "MaaS";
	}

	@Override
	String getScenarioName() {
		return "Scenario 3";
	}

	@Override
	void addUncertaintySources() {
		analysis.getUncertaintySources().addInterfaceUncertaintyInSignature("_dVFMoFiyEe-1dYFuXzLZMA"); // U3
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// U3
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

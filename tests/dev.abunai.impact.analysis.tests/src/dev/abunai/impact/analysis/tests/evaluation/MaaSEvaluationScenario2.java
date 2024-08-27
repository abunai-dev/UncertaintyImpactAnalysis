package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class MaaSEvaluationScenario2  extends EvaluationBase {
	
	@Override
	protected String getBaseFolder() {
		return "casestudies";
	}


	@Override
	protected String getFolderName() {
		return "MaaS_UncertaintyScenario2";
	}

	@Override
	protected String getFilesName() {
		return "MaaS";
	}

	@Override
	String getScenarioName() {
		return "Scenario 2";
	}

	@Override
	void addUncertaintySources() {
		analysis.getUncertaintySources().addBehaviorUncertaintyInSetVariableAction("_y87REFitEe-1dYFuXzLZMA"); // U2
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// U2
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

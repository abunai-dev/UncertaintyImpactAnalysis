package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class CWAEvaluationScenario3 extends EvaluationBase {

	@Override
	protected String getFolderName() {
		return "CoronaWarnApp_UncertaintyScenario3";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Override
	String getScenarioName() {
		return "Scenario 3";
	}

	@Override
	void addUncertaintySources() {
		// Scenario 3: "Focus on behavior, as critical regarding confidentiality"
		analysis.getUncertaintySources().addBehaviorUncertaintyInSetVariableAction("_gK7oULm8Ee2dIMSi7oNVYQ"); // S3_1
		analysis.getUncertaintySources().addBehaviorUncertaintyInExternalCallAction("_kSKnoLm1Ee2dIMSi7oNVYQ"); // S3_2
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// S3_1
			if (dataLiterals.contains("ConfidentialDataNotExpected") & nodeLiterals.contains("Laboratory")) {
				return true;
			}

			// S3_2
			if (dataLiterals.contains("ValidationFailed")) {
				return true;
			}

			return false;
		};
	}

}

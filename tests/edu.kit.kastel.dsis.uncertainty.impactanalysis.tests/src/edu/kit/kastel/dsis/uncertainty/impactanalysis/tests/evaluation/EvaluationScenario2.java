package edu.kit.kastel.dsis.uncertainty.impactanalysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class EvaluationScenario2 extends EvaluationBase {


	@Override
	protected String getFolderName() {
		return "CWA_Scenario2";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Override
	String getScenarioName() {
		return "Scenario 2";
	}

	@Override
	void addUncertaintySources() {
		// Scenario 2: "Environmental uncertainty in the deployment and system context"
		analysis.addActorUncertainty("_N1yckMP6Ee2NifGpaUwYsQ"); // S2_1 (Characteristic IllegalDepl of Portal server)
		analysis.addBehaviorUncertainty("_YIPkQLm8Ee2dIMSi7oNVYQ"); // S2_2 (SEFF registerResult, external Call zu DB Add)
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
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

package edu.kit.kastel.dsis.uncertainty.impactanalysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

public class EvaluationScenario4 extends EvaluationBase {


	@Override
	protected String getFolderName() {
		return "CWA_Scenario4";
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
		analysis.addComponentUncertainty("_v1LV8LNhEe2o46d27a6tVQ"); // S4_1
		analysis.addInterfaceUncertainty("_FC4gkLHzEe2fRLFFhL_FWA"); // S4_2
	}

	@Override
	BiPredicate<List<String>, List<String>> getConstraint() {
		return (List<String> dataLiterals, List<String> nodeLiterals) -> {
			// S4_1
			if(dataLiterals.contains("KeyIssue")) {
				return true;
			}
			
			// S4_2
			if(dataLiterals.contains("RetrievedConfidentialDetails")) {
				return true;
			}
			
			return false;
		};
	}
	
}

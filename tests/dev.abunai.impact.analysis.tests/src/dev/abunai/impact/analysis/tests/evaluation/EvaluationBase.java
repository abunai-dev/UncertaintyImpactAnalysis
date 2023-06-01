package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.PCMActionSequence;

import dev.abunai.impact.analysis.tests.TestBase;

public abstract class EvaluationBase extends TestBase {

	abstract void addUncertaintySources();

	abstract String getScenarioName();

	abstract BiPredicate<List<String>, List<String>> getConstraint();
	
	@Override
	protected String getBaseFolder() {
		return "casestudies/CaseStudy-CoronaWarnApp";
	}

	@Test
	public void evaluateScenario() {
		addUncertaintySources();

		// Do uncertainty impact analysis
		var result = analysis.propagate();
		printResultsWithTitle(result, getScenarioName(), true);

		// Do confidentiality analysis
		var actionSequences = analysis.findAllSequences();
		var evaluatedSequences = analysis.evaluateDataFlows(actionSequences);

		System.out.println("Confidentiality Violations: ");
		for (int i = 0; i < evaluatedSequences.size(); i++) {
			var violations = analysis.queryDataFlow(evaluatedSequences.get(i), it -> {

				List<String> dataLiterals = it.getAllDataFlowVariables().stream().map(e -> e.getAllCharacteristics())
						.flatMap(List::stream).map(e -> e.characteristicLiteral().getName()).toList();
				List<String> nodeLiterals = it.getAllNodeCharacteristics().stream()
						.map(e -> e.characteristicLiteral().getName()).toList();

				return getConstraint().test(dataLiterals, nodeLiterals);
			});

			if (!violations.isEmpty()) {
				System.out.println(formatDataFlow(i, new PCMActionSequence(violations), true));
			}
		}
	}

	@Disabled
	@Test
	public void printAllDataFlows() {
		var actionSequences = analysis.findAllSequences();
		
		System.out.println("All data flows:");
		
		for (int i = 0; i < actionSequences.size(); i++) {
			System.out.println(formatDataFlow(i, new PCMActionSequence(actionSequences.get(i)), true));
		}
	}
}
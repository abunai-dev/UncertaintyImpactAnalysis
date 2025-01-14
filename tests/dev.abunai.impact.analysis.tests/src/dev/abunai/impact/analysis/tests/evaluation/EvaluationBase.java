package dev.abunai.impact.analysis.tests.evaluation;

import java.util.List;
import java.util.function.BiPredicate;

import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
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
		result.printResultsWithTitle(getScenarioName(), true);

		// Do confidentiality analysis
		var flowGraphs = analysis.findFlowGraphs();
		flowGraphs.evaluate();

		System.out.println("Confidentiality Violations: ");
		for (int i = 0; i < flowGraphs.getTransposeFlowGraphs().size(); i++) {
			var violations = analysis.queryDataFlow(flowGraphs.getTransposeFlowGraphs().get(i), it -> {

				List<String> dataLiterals = it.getAllDataCharacteristics().stream().map(e -> e.getAllCharacteristics())
						.flatMap(List::stream).map(e -> e.getValueName()).toList();
				List<String> nodeLiterals = it.getAllVertexCharacteristics().stream()
						.map(e -> e.getValueName()).toList();

				return getConstraint().test(dataLiterals, nodeLiterals);
			});

			if (!violations.isEmpty()) {
				System.out.println(
						UncertaintyImpactCollection.formatDataFlow(i, violations, true));
			}
		}
	}

	//@Disabled
	@Test
	public void printAllDataFlows() {
		var flowGraphs = analysis.findFlowGraphs();

		System.out.println("All data flows:");

		for (int i = 0; i < flowGraphs.getTransposeFlowGraphs().size(); i++) {
			System.out.println(
					UncertaintyImpactCollection.formatDataFlow(i, (PCMTransposeFlowGraph) flowGraphs.getTransposeFlowGraphs().get(i), true));
		}
	}
}
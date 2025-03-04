package dev.abunai.impact.analysis.tests.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import dev.abunai.impact.analysis.tests.ImpactAnnotator;
import org.dataflowanalysis.analysis.core.AbstractTransposeFlowGraph;
import org.dataflowanalysis.analysis.core.AbstractVertex;
import org.dataflowanalysis.analysis.core.CharacteristicValue;
import org.dataflowanalysis.analysis.core.DataCharacteristic;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.junit.jupiter.api.Test;


import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.tests.TestBase;

public abstract class EvaluationBase extends TestBase {
	@Override
	protected String getBaseFolder() {
		return "";
	}

	protected abstract void addUncertaintySources();

	protected abstract String getScenarioName();

	protected abstract BiPredicate<List<String>, List<String>> getConstraint();

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

				List<String> dataLiterals = it.getAllDataCharacteristics().stream()
						.map(DataCharacteristic::getAllCharacteristics)
						.flatMap(List::stream).map(CharacteristicValue::getValueName)
						.toList();
				List<String> nodeLiterals = it.getAllVertexCharacteristics().stream()
						.map(CharacteristicValue::getValueName)
						.toList();

				return getConstraint().test(dataLiterals, nodeLiterals);
			});
			if (!violations.isEmpty()) {
				System.out.println(
						UncertaintyImpactCollection.formatDataFlow(i, violations, true));
			}
		}
	}

	@Test
	public void storeAnnotatedResult() {
		addUncertaintySources();

		// Do uncertainty impact analysis
		var result = analysis.propagate();
		result.printResultsWithTitle(getScenarioName(), true);

		// Do confidentiality analysis
		var flowGraphs = analysis.findFlowGraphs();
		flowGraphs.evaluate();

		var impactAnnotator = new ImpactAnnotator(analysis, result, this.getConstraint());
		var resultWeb = impactAnnotator.getAnnotatedResult();
		impactAnnotator.saveAnnotatedWebDFD(resultWeb, getFolderName() + ".json");
	}

	@Test
	public void printAllDataFlows() {
		var flowGraphs = analysis.findFlowGraphs();

		System.out.println("All data flows:");

		for (int i = 0; i < flowGraphs.getTransposeFlowGraphs().size(); i++) {
			System.out.println(
					UncertaintyImpactCollection.formatDataFlow(i, (PCMTransposeFlowGraph) flowGraphs.getTransposeFlowGraphs().get(i), true));
		}
	}

	private static <T> Predicate<T> distinctByKey(
			Function<? super T, ?> keyExtractor) {

		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private PCMTransposeFlowGraph getAffectedDataFlowSection(PCMTransposeFlowGraph dataflow, List<? extends AbstractPCMVertex<?>> violations) {
		List<? extends AbstractPCMVertex<?>> affectedElements = dataflow.getVertices().stream()
				.filter(AbstractPCMVertex.class::isInstance)
				.map(it -> (AbstractPCMVertex<?>) it)
				.dropWhile(it -> !violations.contains(it)).toList();

		Map<AbstractPCMVertex<?>, AbstractPCMVertex<?>> mapping = new HashMap<>();
		PCMTransposeFlowGraph result = dataflow.copy(mapping);
		for (var vertex : affectedElements) {
			var replacingVertex = mapping.get(vertex);
			List<AbstractPCMVertex<?>> replacingPreviousVertices = new ArrayList<>();
			for (var previousVertex : vertex.getPreviousElements()) {
				var replacingPreviousVertex = mapping.get(previousVertex);
				if (affectedElements.contains(previousVertex)) {
					replacingPreviousVertices.add(replacingPreviousVertex);
				}
			}
			replacingVertex.setPreviousElements(replacingPreviousVertices);
		}
		return result;
	}

	@Test
	public void printMetrics() {
		addUncertaintySources();

		// Do uncertainty impact analysis
		var result = analysis.propagate();
		result.printResultsWithTitle(getScenarioName(), true);

		// Do confidentiality analysis
		var flowGraphs = analysis.findFlowGraphs();
		flowGraphs.evaluate();

		HashMap<Integer, List<? extends AbstractVertex<?>>> violationPerFlowGraph = new HashMap<>();
		for (int i = 0; i < flowGraphs.getTransposeFlowGraphs().size(); i++) {
			var violations = analysis.queryDataFlow(flowGraphs.getTransposeFlowGraphs().get(i), it -> {

				List<String> dataLiterals = it.getAllDataCharacteristics().stream()
						.map(DataCharacteristic::getAllCharacteristics)
						.flatMap(List::stream)
						.map(CharacteristicValue::getValueName)
						.toList();
				List<String> nodeLiterals = it.getAllVertexCharacteristics().stream()
						.map(CharacteristicValue::getValueName)
						.toList();

				return getConstraint().test(dataLiterals, nodeLiterals);
			});
			violationPerFlowGraph.put(i, violations);
		}

		var impactSet = result.getImpactSet(true);
		List<AbstractTransposeFlowGraph> originalImpactedTFGs = new ArrayList<>();
		for (var impactedTFG : impactSet) {
			originalImpactedTFGs.add(flowGraphs.getTransposeFlowGraphs().get(result.getFlowGraphIndex(impactedTFG.getVertices())));
		}

		int impactedDataFlows = impactSet.size();
		long totalElements = originalImpactedTFGs.stream()
				.map(AbstractTransposeFlowGraph::getVertices)
				.mapToLong(List::size)
				.sum();
		long totalUniqueElements = originalImpactedTFGs.stream()
				.map(AbstractTransposeFlowGraph::getVertices)
				.flatMap(List::stream)
				.map(it -> (AbstractPCMVertex<?>) it)
				.filter(distinctByKey(AbstractVertex::toString))
				.count();

		long impactedElements = impactSet.stream()
				.map(AbstractTransposeFlowGraph::getVertices)
				.mapToLong(List::size)
				.sum();
		var impactedUniqueElements = impactSet.stream()
				.map(AbstractTransposeFlowGraph::getVertices)
				.flatMap(List::stream)
				.map(it -> (AbstractPCMVertex<?>) it)
				.filter(distinctByKey(AbstractVertex::toString))
				.toList();

		long violatingElements = violationPerFlowGraph.entrySet().stream()
				.filter((entry) -> !entry.getValue().isEmpty())
				.map(entry -> {
					var tfg = (PCMTransposeFlowGraph) flowGraphs.getTransposeFlowGraphs().get(entry.getKey());
					var violations = entry.getValue().stream()
							.map(it -> (AbstractPCMVertex<?>) it)
							.toList();
					return this.getAffectedDataFlowSection(tfg, violations);
				})
				.map(AbstractTransposeFlowGraph::getVertices)
				.mapToLong(List::size)
				.sum();
		var violatingUniqueElements = violationPerFlowGraph.entrySet().stream()
				.filter((entry) -> !entry.getValue().isEmpty())
				.map(entry -> {
					var tfg = (PCMTransposeFlowGraph) flowGraphs.getTransposeFlowGraphs().get(entry.getKey());
					var violations = entry.getValue().stream()
							.map(it -> (AbstractPCMVertex<?>) it)
							.toList();
					return this.getAffectedDataFlowSection(tfg, violations);
				})
				.map(AbstractTransposeFlowGraph::getVertices)
				.flatMap(List::stream)
				.map(it -> (AbstractPCMVertex<?>) it)
				.filter(distinctByKey(AbstractVertex::toString))
				.toList();

		long truePositives = violatingUniqueElements.size();
		long falsePositives = impactedUniqueElements.size() - violatingUniqueElements.size();
		long falseNegatives = violatingUniqueElements.stream()
				.map(AbstractPCMVertex::getReferencedElement)
				.filter(it -> impactedUniqueElements.stream()
                        .map(AbstractPCMVertex::getReferencedElement)
                        .noneMatch(el -> el.equals(it)))
				.count();

		double precision = (double) truePositives / (truePositives + falsePositives);
		double recall = (double) truePositives / (truePositives + falseNegatives);
		double f1Score = 2 * (precision * recall) / (precision + recall);

		double ratioAffectedSet = (double) violatingUniqueElements.size() / totalUniqueElements;
		double ratioImpactSet = (double) impactedUniqueElements.size() / totalUniqueElements;

		System.out.printf("""
                ------------------  Metrics  ------------------
                Impacted data flows:                    %d
                Total (unique) elements:                %d (%d)
                Impacted (unique) elements:             %d (%d)
                Violating (unique) elements:            %d (%d)
                -----------------------------------------------
                True Positives:                         %d
                False Positives:                        %d
                False Negatives:                        %d
                -----------------------------------------------
                Precision:                              %.3f
                Recall:                                 %.3f
                F1-Score:                               %.3f
                -----------------------------------------------
                Ratio of the actual impact set:         %.3f
                Ratio of the uncertainty impact set:    %.3f
                %n""", impactedDataFlows, totalElements, totalUniqueElements, impactedElements, impactedUniqueElements.size(), violatingElements, violatingUniqueElements.size(), truePositives, falsePositives, falseNegatives, precision, recall, f1Score, ratioAffectedSet, ratioImpactSet);
	}
}

package dev.abunai.impact.analysis.tests.evaluation;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import dev.abunai.impact.analysis.testmodels.Activator;
import org.dataflowanalysis.analysis.core.CharacteristicValue;
import org.dataflowanalysis.analysis.core.DataCharacteristic;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.dataflowanalysis.converter.DataFlowDiagramConverter;
import org.dataflowanalysis.converter.PCMConverter;
import org.dataflowanalysis.converter.WebEditorConverter;
import org.dataflowanalysis.converter.webdfd.Annotation;
import org.dataflowanalysis.dfd.dataflowdiagram.Flow;
import org.dataflowanalysis.dfd.dataflowdiagram.Node;
import org.junit.jupiter.api.Disabled;
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

		var converter = new PCMConverter();
		final var usageModelPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".usagemodel")
				.toString();
		final var allocationPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".allocation")
				.toString();
		final var nodeCharacteristicsPath = Paths
				.get(getBaseFolder(), getFolderName(), getFilesName() + ".nodecharacteristics").toString();
		var resultDFD = converter.pcmToDFD("dev.abunai.impact.analysis.testmodels", usageModelPath, allocationPath, nodeCharacteristicsPath, Activator.class);

		var annotateConverter = new DataFlowDiagramConverter();
		int counter = 0;
		for (var node : resultDFD.dataFlowDiagram().getNodes()) {
			node.setEntityName(String.valueOf(counter++));
			node.getProperties().clear();
		}
		Map<String, Node> followingVertices = new HashMap<>();
		List<Flow> removedFlows = new ArrayList<>();
		for (var flow : resultDFD.dataFlowDiagram().getFlows()) {
			flow.setEntityName("");
			if (!followingVertices.containsKey(flow.getSourceNode().getId() + flow.getDestinationNode().getId())) {
				followingVertices.put(flow.getSourceNode().getId() + flow.getDestinationNode().getId(), flow.getDestinationNode());
			} else {
				removedFlows.add(flow);
			}
		}
		resultDFD.dataFlowDiagram().getFlows().removeAll(removedFlows);
		for (var removedFlow : removedFlows) {
			var cleanedAssignmentsSource = removedFlow.getSourceNode().getBehaviour().getAssignment().stream()
					.filter(it -> !it.getOutputPin().equals(removedFlow.getSourcePin()))
					.toList();
			removedFlow.getSourceNode().getBehaviour().getAssignment().clear();
			removedFlow.getSourceNode().getBehaviour().getAssignment().addAll(cleanedAssignmentsSource);


			var cleanedAssignmentsDestination = removedFlow.getDestinationNode().getBehaviour().getAssignment().stream()
					.filter(it -> !it.getInputPins().contains(removedFlow.getDestinationPin()))
					.toList();
			removedFlow.getDestinationNode().getBehaviour().getAssignment().clear();
			removedFlow.getDestinationNode().getBehaviour().getAssignment().addAll(cleanedAssignmentsDestination);
			if (resultDFD.dataFlowDiagram().getFlows().stream().noneMatch(it -> it.getSourcePin().equals(removedFlow.getSourcePin()))) {
				removedFlow.getSourceNode().getBehaviour().getOutPin().remove(removedFlow.getSourcePin());
			}
			if (resultDFD.dataFlowDiagram().getFlows().stream().noneMatch(it -> it.getDestinationPin().equals(removedFlow.getDestinationPin()))) {
				removedFlow.getDestinationNode().getBehaviour().getInPin().remove(removedFlow.getDestinationPin());
			}
		}
		Map<Node, Annotation> annotations = new HashMap<>();
		for (var sequence : result.getImpactSet(false)) {
			for (AbstractPCMVertex<?> vertex : sequence.getVertices().stream().map(it -> (AbstractPCMVertex<?>) it).toList()) {
				var nodes = resultDFD.dataFlowDiagram().getNodes().stream()
						.filter(it -> it.getId().contains(vertex.getReferencedElement().getId()))
						.toList();
				for (var node : nodes) {
					annotations.put(node, new Annotation("Impacted element", "bolt", "#a3107c"));
				}
			}
		}

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
			for (var vertex : violations.stream().map(it -> (AbstractPCMVertex<?>) it).toList()) {
				var nodes = resultDFD.dataFlowDiagram().getNodes().stream()
						.filter(it -> it.getId().contains(vertex.getReferencedElement().getId()))
						.toList();
				for (var node : nodes) {
					if (annotations.containsKey(node)) {
						annotations.put(node, new Annotation("Violating element", "bolt", "#a22223"));
					}
				}
			}

			if (!violations.isEmpty()) {
				System.out.println(
						UncertaintyImpactCollection.formatDataFlow(i, violations, true));
			}
		}
		var resultWeb = annotateConverter.dfdToWeb(resultDFD, annotations);
		annotateConverter.storeWeb(resultWeb, getFolderName() + ".json");
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

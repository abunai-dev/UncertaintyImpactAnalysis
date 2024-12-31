package dev.abunai.impact.analysis.tests;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;
import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.testmodels.Activator;
import org.dataflowanalysis.analysis.core.CharacteristicValue;
import org.dataflowanalysis.analysis.core.DataCharacteristic;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.converter.DataFlowDiagramConverter;
import org.dataflowanalysis.converter.PCMConverter;
import org.dataflowanalysis.converter.webdfd.Annotation;
import org.dataflowanalysis.converter.webdfd.WebEditorDfd;
import org.dataflowanalysis.dfd.datadictionary.Assignment;
import org.dataflowanalysis.dfd.dataflowdiagram.Flow;
import org.dataflowanalysis.dfd.dataflowdiagram.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class ImpactAnnotator {
    private final PCMUncertaintyImpactAnalysis analysis;
    private final UncertaintyImpactCollection impact;
    private final BiPredicate<List<String>, List<String>> constraint;

    private final DataFlowDiagramConverter annotateConverter = new DataFlowDiagramConverter();

    public ImpactAnnotator(PCMUncertaintyImpactAnalysis analysis, UncertaintyImpactCollection impact, BiPredicate<List<String>, List<String>> constraint) {
        this.analysis = analysis;
        this.impact = impact;
        this.constraint = constraint;
    }


    public WebEditorDfd getAnnotatedResult(String usageModelPath, String allocationPath, String nodeCharacteristicsPath) {
        var converter = new PCMConverter();
        var flowGraphs = this.analysis.findFlowGraphs();
        flowGraphs.evaluate();
        var resultDFD = converter.pcmToDFD("dev.abunai.impact.analysis.testmodels", usageModelPath, allocationPath, nodeCharacteristicsPath, Activator.class);

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
            var cleanedAssignmentsSource = removedFlow.getSourceNode().getBehavior().getAssignment().stream()
                    .filter(it -> !it.getOutputPin().equals(removedFlow.getSourcePin()))
                    .toList();
            removedFlow.getSourceNode().getBehavior().getAssignment().clear();
            removedFlow.getSourceNode().getBehavior().getAssignment().addAll(cleanedAssignmentsSource);


            var cleanedAssignmentsDestination = removedFlow.getDestinationNode().getBehavior().getAssignment().stream()
                    .map(it -> (Assignment) it)
                    .filter(it -> !it.getInputPins().contains(removedFlow.getDestinationPin()))
                    .toList();
            removedFlow.getDestinationNode().getBehavior().getAssignment().clear();
            removedFlow.getDestinationNode().getBehavior().getAssignment().addAll(cleanedAssignmentsDestination);
            if (resultDFD.dataFlowDiagram().getFlows().stream().noneMatch(it -> it.getSourcePin().equals(removedFlow.getSourcePin()))) {
                removedFlow.getSourceNode().getBehavior().getOutPin().remove(removedFlow.getSourcePin());
            }
            if (resultDFD.dataFlowDiagram().getFlows().stream().noneMatch(it -> it.getDestinationPin().equals(removedFlow.getDestinationPin()))) {
                removedFlow.getDestinationNode().getBehavior().getInPin().remove(removedFlow.getDestinationPin());
            }
        }
        Map<Node, Annotation> annotations = new HashMap<>();
        for (var sequence : impact.getImpactSet(false)) {
            for (AbstractPCMVertex<?> vertex : sequence.getVertices().stream().map(it -> (AbstractPCMVertex<?>) it).toList()) {
                var nodes = resultDFD.dataFlowDiagram().getNodes().stream()
                        .filter(it -> it.getId().contains(vertex.getReferencedElement().getId()))
                        .toList();
                for (var node : nodes) {
                    annotations.put(node, new Annotation("Impacted element", "bolt", "#a3107c"));
                }
            }
        }
        for (int i = 0; i < flowGraphs.getTransposeFlowGraphs().size(); i++) {
            var violations = analysis.queryDataFlow(flowGraphs.getTransposeFlowGraphs().get(i), it -> {

                List<String> dataLiterals = it.getAllDataCharacteristics().stream()
                        .map(DataCharacteristic::getAllCharacteristics)
                        .flatMap(List::stream).map(CharacteristicValue::getValueName)
                        .toList();
                List<String> nodeLiterals = it.getAllVertexCharacteristics().stream()
                        .map(CharacteristicValue::getValueName)
                        .toList();

                return this.constraint.test(dataLiterals, nodeLiterals);
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
        }
        return annotateConverter.dfdToWeb(resultDFD, annotations);
    }

    public void saveAnnotatedWebDFD(WebEditorDfd web, String path) {
        annotateConverter.storeWeb(web, path);
    }
}

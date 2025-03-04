package dev.abunai.impact.analysis.tests;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;
import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import org.apache.log4j.Logger;
import org.dataflowanalysis.analysis.core.AbstractTransposeFlowGraph;
import org.dataflowanalysis.analysis.core.AbstractVertex;
import org.dataflowanalysis.analysis.core.CharacteristicValue;
import org.dataflowanalysis.analysis.core.DataCharacteristic;
import org.dataflowanalysis.analysis.core.FlowGraphCollection;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
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
    private static final Logger log = Logger.getLogger(ImpactAnnotator.class);
    private final PCMUncertaintyImpactAnalysis analysis;
    private final UncertaintyImpactCollection impact;
    private final BiPredicate<List<String>, List<String>> constraint;

    private final DataFlowDiagramConverter annotateConverter = new DataFlowDiagramConverter();

    public ImpactAnnotator(PCMUncertaintyImpactAnalysis analysis, UncertaintyImpactCollection impact, BiPredicate<List<String>, List<String>> constraint) {
        this.analysis = analysis;
        this.impact = impact;
        this.constraint = constraint;
    }

    private int getIndex(FlowGraphCollection flowGraphs, List<? extends AbstractVertex<?>> entries) {
        for (int i = 0; i < flowGraphs.getTransposeFlowGraphs().size(); i++) {
            var elements = flowGraphs.getTransposeFlowGraphs().get(i).getVertices().stream()
                    .map(it -> (AbstractPCMVertex<?>) it).toList();

            boolean matches = true;
            for (var entry : entries) {
                if (elements.stream().noneMatch(it -> it.isEquivalentInContext(entry))) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                return i;
            }
        }
        return -1;
    }

    private boolean matches(AbstractPCMVertex<?> mapVertex, AbstractTransposeFlowGraph mapTransposeFlowGraph, FlowGraphCollection flowGraphCollection, AbstractPCMVertex<?> impactVertex, PCMTransposeFlowGraph transposeFlowGraph) {
        if (!mapVertex.isEquivalentInContext(impactVertex)) {
            return false;
        }
        int flowGraphIndex = getIndex(flowGraphCollection, mapTransposeFlowGraph.getVertices());
        int impactSetIndex = impact.getFlowGraphIndex(transposeFlowGraph.getVertices());
        return flowGraphIndex == impactSetIndex;
    }


    public WebEditorDfd getAnnotatedResult() {
        var converter = new PCMConverter();
        var flowGraphs = this.analysis.findFlowGraphs();
        flowGraphs.evaluate();
        var resultDFD = converter.pcmToDFD(flowGraphs);

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
                    .filter(Assignment.class::isInstance)
                    .map(Assignment.class::cast)
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
        var impactSet = impact.getImpactSet(false);
        for (var transposeFlowGraph : impactSet) {
            for (AbstractPCMVertex<?> vertex : transposeFlowGraph.getVertices().stream().map(it -> (AbstractPCMVertex<?>) it).toList()) {
                var node = converter.getDfdNodeMap().entrySet().stream()
                        .filter(it -> this.matches(it.getKey(), converter.getPcmFlowMap().get(it.getKey()), flowGraphs, vertex, transposeFlowGraph))
                        .map(Map.Entry::getValue)
                        .findFirst().orElseThrow(() -> new IllegalStateException("Cant find " + vertex));
                annotations.put(node, new Annotation("Impacted element", "bolt", "#a3107c"));
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
                var node = converter.getDfdNodeMap().get(vertex);
                if (node == null) {
                    log.warn("Could not find violating node:" + vertex);
                }
                if (annotations.containsKey(node)) {
                    annotations.put(node, new Annotation("Violating element", "bolt", "#a22223"));
                }
            }
        }
        int counter = 0;
        for (var node : resultDFD.dataFlowDiagram().getNodes()) {
            node.setEntityName(String.valueOf(counter++));
            node.getProperties().clear();
        }
        return annotateConverter.dfdToWeb(resultDFD, annotations);
    }

    public void saveAnnotatedWebDFD(WebEditorDfd web, String path) {
        annotateConverter.storeWeb(web, path);
    }
}

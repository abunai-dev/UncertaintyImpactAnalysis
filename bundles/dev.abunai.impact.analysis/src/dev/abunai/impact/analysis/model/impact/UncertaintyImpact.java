package dev.abunai.impact.analysis.model.impact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dataflowanalysis.analysis.core.AbstractVertex;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.CallReturnBehavior;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.dataflowanalysis.analysis.pcm.core.seff.CallingSEFFPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.seff.SEFFPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.user.CallingUserPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.user.UserPCMVertex;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.source.UncertaintySource;

public abstract class UncertaintyImpact<T extends Entity> {

	public abstract UncertaintySource<T> getOrigin();

	public abstract AbstractPCMVertex<?> getAffectedElement();

	public abstract List<PCMTransposeFlowGraph> getAffectedDataFlows();

	private PCMTransposeFlowGraph getAffectedDataFlowSectionOf(PCMTransposeFlowGraph dataflow) {

		List<? extends AbstractPCMVertex<?>> affectedElements = dataflow.getVertices().stream()
				.filter(AbstractPCMVertex.class::isInstance)
				.map(it -> (AbstractPCMVertex<?>) it)
				.dropWhile(it -> !it.equals(this.getAffectedElement())).toList();

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

	public List<PCMTransposeFlowGraph> getAffectedDataFlowSections() {
		var affectedDataFlows = this.getAffectedDataFlows();

		return affectedDataFlows.stream().map(it -> getAffectedDataFlowSectionOf(it)).toList();
	}

	@Override
	public String toString() {
		var generalInfo = String.format("%s Uncertainty Impact on %s with ID %s (represeting a %s).",
				this.getOrigin().getUncertaintyType(), this.getAffectedElement().getClass().getSimpleName(),
				EcoreUtil.getID(this.getAffectedElement().getReferencedElement()),
				this.getAffectedElement().getReferencedElement().getClass().getSimpleName());
		var originInfo = String.format("Origin of this impact: %s", this.getOrigin().toString());

		var dataFlowInfo = "";

		for (var affectedDataFlow : this.getAffectedDataFlows()) {
			var affectedDataFlowInfo = String.format("Affected Data Flows: %s",
					affectedDataFlow.getVertices().stream().map(AbstractVertex::toString).collect(Collectors.joining(", ")));
			var affectedDataFlowElementIndex = String.format("Affected Element Index: %d",
					affectedDataFlow.getVertices().indexOf(this.getAffectedElement()));
			var affectedDataFlowSectionInfo = String.format("Affected Data Flow Section: %s",
					this.getAffectedDataFlowSectionOf(affectedDataFlow).getVertices().stream().map(AbstractVertex::toString)
							.collect(Collectors.joining(", ")));
			var emptyLine = "";

			dataFlowInfo += String.join(affectedDataFlowInfo, affectedDataFlowElementIndex, affectedDataFlowSectionInfo,
					emptyLine);
		}

		return String.join(System.lineSeparator(), generalInfo, originInfo, dataFlowInfo);
	}
}

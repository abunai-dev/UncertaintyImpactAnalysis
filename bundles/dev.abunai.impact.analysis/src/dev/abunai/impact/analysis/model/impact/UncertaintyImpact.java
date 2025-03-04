package dev.abunai.impact.analysis.model.impact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dataflowanalysis.analysis.core.AbstractVertex;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.source.UncertaintySource;

/**
 * Represents of an uncertainty impact on a given element with type {@link T} caused by an uncertainty source
 * @param <T> Type parameter of the affected element
 */
public abstract class UncertaintyImpact<T extends Entity> {

	/**
	 * Returns the {@link UncertaintySource} that caused the {@link UncertaintyImpact}.
	 * The abstract type parameter of the {@link UncertaintySource} must be equal to the type parameter of the {@link UncertaintyImpact}
	 * @return Returns the {@link UncertaintySource} that caused the {@link UncertaintyImpact}
	 */
	public abstract UncertaintySource<T> getOrigin();

	/**
	 * Returns the PCM element that is affected by the {@link UncertaintyImpact}
	 * @return Returns the affected PCM element affected by the {@link UncertaintyImpact}
	 */
	public abstract AbstractPCMVertex<?> getAffectedElement();

	/**
	 * Returns the list of affected data flows caused by the {@link UncertaintyImpact}
	 * @return Returns the list of affected transpose flow graphs that are affected by the {@link UncertaintyImpact}
	 */
	public abstract List<PCMTransposeFlowGraph> getAffectedDataFlows();

	/**
	 * Returns the affected data flow section of the given transpose flow graph.
	 * It starts at the affected element towards the end of the transpose flow graph
	 * @param dataflow Given transpose flow graph of which the section should be created
	 * @return Returns the affected data flow section of the given transpose flow graph of the given {@link UncertaintyImpact}
	 */
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

	/**
	 * Returns the affected data flow sections of all affected transpose flow graphs
	 * @return Returns the affected data flow sections of affected transpose flow graphs of the given {@link UncertaintyImpact}
	 */
	public List<PCMTransposeFlowGraph> getAffectedDataFlowSections() {
		var affectedDataFlows = this.getAffectedDataFlows();
		return affectedDataFlows.stream()
				.map(this::getAffectedDataFlowSectionOf)
				.toList();
	}

	@Override
	public String toString() {
		var generalInfo = String.format("%s Uncertainty Impact on %s with ID %s (represeting a %s).",
				this.getOrigin().getUncertaintyType(), this.getAffectedElement().getClass().getSimpleName(),
				EcoreUtil.getID(this.getAffectedElement().getReferencedElement()),
				this.getAffectedElement().getReferencedElement().getClass().getSimpleName());
		var originInfo = String.format("Origin of this impact: %s", this.getOrigin().toString());

		StringBuilder dataFlowInfo = new StringBuilder();

		for (var affectedDataFlow : this.getAffectedDataFlows()) {
			var affectedDataFlowInfo = String.format("Affected Data Flows: %s",
					affectedDataFlow.getVertices().stream().map(AbstractVertex::toString).collect(Collectors.joining(", ")));
			var affectedDataFlowElementIndex = String.format("Affected Element Index: %d",
					affectedDataFlow.getVertices().indexOf(this.getAffectedElement()));
			var affectedDataFlowSectionInfo = String.format("Affected Data Flow Section: %s",
					this.getAffectedDataFlowSectionOf(affectedDataFlow).getVertices().stream().map(AbstractVertex::toString)
							.collect(Collectors.joining(", ")));
			var emptyLine = "";

			dataFlowInfo.append(String.join(affectedDataFlowInfo, affectedDataFlowElementIndex, affectedDataFlowSectionInfo,
                    emptyLine));
		}

		return String.join(System.lineSeparator(), generalInfo, originInfo, dataFlowInfo.toString());
	}
}

package dev.abunai.impact.analysis.model.impact;

import java.util.List;
import java.util.stream.Collectors;

import org.dataflowanalysis.analysis.core.AbstractActionSequenceElement;
import org.dataflowanalysis.analysis.core.ActionSequence;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMActionSequenceElement;
import org.dataflowanalysis.analysis.pcm.core.PCMActionSequence;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.source.UncertaintySource;

public abstract class UncertaintyImpact<T extends Entity> {

	public abstract UncertaintySource<T> getOrigin();

	public abstract AbstractPCMActionSequenceElement<?> getAffectedElement();

	public abstract List<ActionSequence> getAffectedDataFlows();

	private ActionSequence getAffectedDataFlowSectionOf(ActionSequence dataflow) {

		List<AbstractActionSequenceElement<?>> affectedElements = dataflow.getElements().stream()
				.dropWhile(it -> !it.equals(this.getAffectedElement())).toList();

		return new PCMActionSequence(affectedElements);

	}

	public List<ActionSequence> getAffectedDataFlowSections() {
		var affectedDataFlows = this.getAffectedDataFlows();

		return affectedDataFlows.stream().map(it -> getAffectedDataFlowSectionOf(it)).toList();
	}

	@Override
	public String toString() {
		var generalInfo = String.format("%s Uncertainty Impact on %s with ID %s (represeting a %s).",
				this.getOrigin().getUncertaintyType(), this.getAffectedElement().getClass().getSimpleName(),
				EcoreUtil.getID(this.getAffectedElement().getElement()),
				this.getAffectedElement().getElement().getClass().getSimpleName());
		var originInfo = String.format("Origin of this impact: %s", this.getOrigin().toString());

		var dataFlowInfo = "";

		for (var affectedDataFlow : this.getAffectedDataFlows()) {
			var affectedDataFlowInfo = String.format("Affected Data Flows: %s",
					affectedDataFlow.getElements().stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
			var affectedDataFlowElementIndex = String.format("Affected Element Index: %d",
					affectedDataFlow.getElements().indexOf(this.getAffectedElement()));
			var affectedDataFlowSectionInfo = String.format("Affected Data Flow Section: %s",
					this.getAffectedDataFlowSectionOf(affectedDataFlow).getElements().stream().map(it -> it.toString())
							.collect(Collectors.joining(", ")));
			var emptyLine = "";

			dataFlowInfo += String.join(affectedDataFlowInfo, affectedDataFlowElementIndex, affectedDataFlowSectionInfo,
					emptyLine);
		}

		return String.join(System.lineSeparator(), generalInfo, originInfo, dataFlowInfo);
	}
}

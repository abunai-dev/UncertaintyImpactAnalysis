package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ActorUncertaintyImpact extends UncertaintyImpact<Entity> {

	private final AbstractPCMVertex<?> affectedElement;
	private final UncertaintySource<Entity> origin;
	private final PropagationHelper propagationHelper;

	public ActorUncertaintyImpact(AbstractPCMVertex<?> affectedElement, UncertaintySource<Entity> origin,
			PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<Entity> getOrigin() {
		return this.origin;
	}

	@Override
	public AbstractPCMVertex<?> getAffectedElement() {
		return this.affectedElement;
	}

	@Override
	public List<PCMTransposeFlowGraph> getAffectedDataFlows() {
		return propagationHelper.findActionSequencesWithElement(affectedElement);
	}

}

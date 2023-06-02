package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ActorUncertaintyImpact extends UncertaintyImpact<Entity>{

	private final AbstractPCMActionSequenceElement<?> affectedElement;
	private final UncertaintySource<Entity> origin;
	private final PropagationHelper propagationHelper;

	public ActorUncertaintyImpact(AbstractPCMActionSequenceElement<?> affectedElement, UncertaintySource<Entity> origin,
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
	public AbstractPCMActionSequenceElement<?> getAffectedElement() {
		return this.affectedElement;
	}

	@Override
	public List<ActionSequence> getAffectedDataFlows() {
		return propagationHelper.findActionSequencesWithElement(affectedElement);
	}

}

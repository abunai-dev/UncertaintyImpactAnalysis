package dev.abunai.impact.analysis.model.impact;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.StartAction;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ComponentUncertaintyImpact extends UncertaintyImpact<AssemblyContext> {

	private final SEFFActionSequenceElement<StartAction> affectedElement;
	private final UncertaintySource<AssemblyContext> origin;
	private final PropagationHelper propagationHelper;

	public ComponentUncertaintyImpact(SEFFActionSequenceElement<StartAction> affectedElement,
			UncertaintySource<AssemblyContext> origin, PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<AssemblyContext> getOrigin() {
		return origin;
	}

	@Override
	public SEFFActionSequenceElement<StartAction> getAffectedElement() {
		return affectedElement;
	}

	@Override
	public List<ActionSequence> getAffectedDataFlows() {
		return propagationHelper.findActionSequencesWithElement(affectedElement);
	}
}

package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import java.util.Optional;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.entity.Entity;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class BehaviorUncertaintyImpact<T extends Entity> extends UncertaintyImpact<T> {

	private final AbstractPCMActionSequenceElement<?> affectedElement;
	private final UncertaintySource<T> origin;
	private final PropagationHelper propagationHelper;

	public BehaviorUncertaintyImpact(AbstractPCMActionSequenceElement<?> affectedElement, UncertaintySource<T> origin,
			PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<T> getOrigin() {
		return origin;
	}

	@Override
	public AbstractPCMActionSequenceElement<?> getAffectedElement() {
		return affectedElement;
	}

	@Override
	public Optional<ActionSequence> getAffectedDataFlow() {
		return propagationHelper.findActionSequenceWithElement(affectedElement);
	}

}

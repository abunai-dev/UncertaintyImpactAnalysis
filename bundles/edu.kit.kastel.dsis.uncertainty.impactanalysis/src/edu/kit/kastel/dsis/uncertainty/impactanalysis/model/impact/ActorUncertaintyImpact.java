package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import java.util.Optional;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class ActorUncertaintyImpact extends UncertaintyImpact<EnumCharacteristic>{

	private final AbstractPCMActionSequenceElement<?> affectedElement;
	private final UncertaintySource<EnumCharacteristic> origin;
	private final PropagationHelper propagationHelper;

	public ActorUncertaintyImpact(AbstractPCMActionSequenceElement<?> affectedElement, UncertaintySource<EnumCharacteristic> origin,
			PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	
	@Override
	public UncertaintySource<EnumCharacteristic> getOrigin() {
		return this.origin;
	}

	@Override
	public AbstractPCMActionSequenceElement<?> getAffectedElement() {
		return this.affectedElement;
	}

	@Override
	public Optional<ActionSequence> getAffectedDataFlow() {
		return propagationHelper.findActionSequenceWithElement(affectedElement);
	}

}

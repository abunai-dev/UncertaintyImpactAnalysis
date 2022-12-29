package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import java.util.Optional;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;

public class ActorUncertaintyImpact extends UncertaintyImpact<EnumCharacteristic>{

	@Override
	public UncertaintySource<EnumCharacteristic> getOrigin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractPCMActionSequenceElement<?> getAffectedElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<ActionSequence> getAffectedDataFlow() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}

package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.ActorUncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class ActorUncertaintySource extends UncertaintySource<EnumCharacteristic> {

	private final EnumCharacteristic characteristicAnnotation;
	private final PropagationHelper propagationHelper;

	public ActorUncertaintySource(EnumCharacteristic characteristicAnnotation, PropagationHelper propagationHelper) {
		Objects.requireNonNull(characteristicAnnotation);
		Objects.requireNonNull(propagationHelper);
		this.characteristicAnnotation = characteristicAnnotation;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public EnumCharacteristic getArchitecturalElement() {
		return this.characteristicAnnotation;
	}

	@Override
	public List<ActorUncertaintyImpact> propagate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUncertaintyType() {
		return "Actor";
	}

}

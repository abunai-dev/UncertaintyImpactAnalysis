package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
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
		List<AbstractPCMActionSequenceElement<?>> processes = propagationHelper.findProcessesWithAnnotation(this.characteristicAnnotation);
		return processes.stream().map(it -> new ActorUncertaintyImpact(it, this, this.propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Actor";
	}
	
	@Override
	public String toString() {
		return String.format("%s Uncertainty annotated to %s \"%s\" (%s).", this.getUncertaintyType(),
				this.getArchitecturalElement().getClass().getSimpleName().replace("Impl", ""),
				this.getArchitecturalElement().getType().getName(), this.getArchitecturalElement().getId());
	};

}

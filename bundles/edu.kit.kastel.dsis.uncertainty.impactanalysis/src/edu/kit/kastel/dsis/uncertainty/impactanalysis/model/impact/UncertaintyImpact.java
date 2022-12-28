package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.pcm.core.entity.Entity;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;

public abstract class UncertaintyImpact<P extends Entity> {

	public abstract UncertaintySource<P> getOrigin();
	
	public abstract AbstractActionSequenceElement<?> getAffectedElement();

	public abstract ActionSequence getAffectedDataFlow();
}

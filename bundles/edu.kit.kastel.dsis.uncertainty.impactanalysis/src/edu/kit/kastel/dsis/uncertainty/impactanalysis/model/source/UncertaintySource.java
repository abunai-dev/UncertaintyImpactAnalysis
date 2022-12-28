package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.UncertaintyImpact;

public abstract class UncertaintySource<P extends Entity> {

	public abstract P getArchitecturalElement();

	public abstract List<? extends UncertaintyImpact<? extends P>> propagate();

}

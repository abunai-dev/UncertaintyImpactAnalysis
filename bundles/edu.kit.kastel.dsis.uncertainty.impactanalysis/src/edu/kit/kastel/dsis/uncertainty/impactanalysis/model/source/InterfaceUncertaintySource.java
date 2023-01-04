package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.pcm.repository.Interface;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.InterfaceUncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class InterfaceUncertaintySource extends UncertaintySource<Interface> {
	
	private final Interface interfaze;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintySource(Interface interfaze, PropagationHelper propagationHelper) {
		Objects.requireNonNull(interfaze);
		Objects.requireNonNull(propagationHelper);
		this.interfaze = interfaze;
		this.propagationHelper = propagationHelper;
	}
	

	@Override
	public Interface getArchitecturalElement() {
		return this.interfaze;
	}

	@Override
	public List<InterfaceUncertaintyImpact> propagate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUncertaintyType() {
		return "Interface";
	}

}

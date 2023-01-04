package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.pcm.repository.OperationInterface;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.InterfaceUncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class InterfaceUncertaintySource extends UncertaintySource<OperationInterface> {
	
	private final OperationInterface interfaze;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintySource(OperationInterface interfaze, PropagationHelper propagationHelper) {
		Objects.requireNonNull(interfaze);
		Objects.requireNonNull(propagationHelper);
		this.interfaze = interfaze;
		this.propagationHelper = propagationHelper;
	}
	

	@Override
	public OperationInterface getArchitecturalElement() {
		return this.interfaze;
	}

	@Override
	public List<InterfaceUncertaintyImpact> propagate() {
		var startNodes = propagationHelper.findStartActionsOfSEFFsThatImplement(this.interfaze);
		var systemCalls = propagationHelper.findEntryLevelSystemCallsViaInterface(this.interfaze);
		var externalCalls = propagationHelper.findExternalCallsViaInterface(this.interfaze);
		
		
		return null;
	}

	@Override
	public String getUncertaintyType() {
		return "Interface";
	}

}

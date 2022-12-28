package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.StartAction;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class ComponentUncertaintyImpact extends UncertaintyImpact<AssemblyContext> {
	
	private final SEFFActionSequenceElement<StartAction> affectedElement;
	private final UncertaintySource<AssemblyContext> origin;
	private final PropagationHelper propagationHelper;
	
	public ComponentUncertaintyImpact(SEFFActionSequenceElement<StartAction> affectedElement, UncertaintySource<AssemblyContext> origin, PropagationHelper propagationHelper) {
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
	public ActionSequence getAffectedDataFlow() {
		return propagationHelper.findActionSequenceWithElement(affectedElement);
	}



}

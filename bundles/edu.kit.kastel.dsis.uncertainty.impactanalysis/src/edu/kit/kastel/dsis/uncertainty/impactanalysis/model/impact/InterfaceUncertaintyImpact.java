package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import java.util.Optional;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.seff.StartAction;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class InterfaceUncertaintyImpact extends UncertaintyImpact<OperationInterface> {

	private final SEFFActionSequenceElement<StartAction> affectedElement;
	private final UncertaintySource<OperationInterface> origin;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintyImpact(SEFFActionSequenceElement<StartAction> affectedElement,
			UncertaintySource<OperationInterface> origin, PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}
	
	@Override
	public UncertaintySource<OperationInterface> getOrigin() {
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

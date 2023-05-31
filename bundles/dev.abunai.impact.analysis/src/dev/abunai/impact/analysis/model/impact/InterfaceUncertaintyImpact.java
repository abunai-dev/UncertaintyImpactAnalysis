package dev.abunai.impact.analysis.model.impact;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.repository.OperationInterface;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class InterfaceUncertaintyImpact extends UncertaintyImpact<OperationInterface> {

	private final AbstractPCMActionSequenceElement<?> affectedElement;
	private final UncertaintySource<OperationInterface> origin;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintyImpact(AbstractPCMActionSequenceElement<?> affectedElement,
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
	public List<ActionSequence> getAffectedDataFlows() {
		return propagationHelper.findActionSequencesWithElement(affectedElement);
	}

}

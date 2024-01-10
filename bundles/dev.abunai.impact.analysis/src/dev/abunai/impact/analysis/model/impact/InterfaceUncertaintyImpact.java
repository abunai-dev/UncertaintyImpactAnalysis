package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.core.ActionSequence;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.repository.OperationSignature;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class InterfaceUncertaintyImpact extends UncertaintyImpact<OperationSignature> {

	private final AbstractPCMActionSequenceElement<?> affectedElement;
	private final UncertaintySource<OperationSignature> origin;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintyImpact(AbstractPCMActionSequenceElement<?> affectedElement,
			UncertaintySource<OperationSignature> origin, PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<OperationSignature> getOrigin() {
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

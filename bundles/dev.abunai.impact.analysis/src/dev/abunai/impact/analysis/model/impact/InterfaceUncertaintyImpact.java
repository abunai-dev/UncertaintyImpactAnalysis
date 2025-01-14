package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.palladiosimulator.pcm.repository.OperationSignature;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class InterfaceUncertaintyImpact extends UncertaintyImpact<OperationSignature> {

	private final AbstractPCMVertex<?> affectedElement;
	private final UncertaintySource<OperationSignature> origin;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintyImpact(AbstractPCMVertex<?> affectedElement,
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
	public AbstractPCMVertex<?> getAffectedElement() {
		return this.affectedElement;
	}

	@Override
	public List<PCMTransposeFlowGraph> getAffectedDataFlows() {
		return propagationHelper.findTransposeFlowGraphsWithElement(affectedElement);
	}

}

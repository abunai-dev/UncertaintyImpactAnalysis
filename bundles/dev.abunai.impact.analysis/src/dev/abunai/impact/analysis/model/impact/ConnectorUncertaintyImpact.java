package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
import org.palladiosimulator.pcm.core.composition.Connector;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ConnectorUncertaintyImpact<T extends Connector> extends UncertaintyImpact<T> {

	private final AbstractPCMActionSequenceElement<?> affectedElement;
	private final UncertaintySource<T> origin;
	private final PropagationHelper propagationHelper;

	public ConnectorUncertaintyImpact(AbstractPCMActionSequenceElement<?> affectedElement, UncertaintySource<T> origin,
			PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<T> getOrigin() {
		return origin;
	}

	@Override
	public AbstractPCMActionSequenceElement<?> getAffectedElement() {
		return affectedElement;
	}

	@Override
	public List<ActionSequence> getAffectedDataFlows() {
		return propagationHelper.findActionSequencesWithElement(affectedElement);
	}

}

package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.palladiosimulator.pcm.core.composition.Connector;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ConnectorUncertaintyImpact<T extends Connector> extends UncertaintyImpact<T> {

	private final AbstractPCMVertex<?> affectedElement;
	private final UncertaintySource<T> origin;
	private final PropagationHelper propagationHelper;

	public ConnectorUncertaintyImpact(AbstractPCMVertex<?> affectedElement, UncertaintySource<T> origin,
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
	public AbstractPCMVertex<?> getAffectedElement() {
		return affectedElement;
	}

	@Override
	public List<PCMTransposeFlowGraph> getAffectedDataFlows() {
		return propagationHelper.findTransposeFlowGraphsWithElement(affectedElement);
	}

}

package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.palladiosimulator.pcm.core.composition.Connector;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents the uncertainty impact of an {@link dev.abunai.impact.analysis.model.source.ConnectorUncertaintySource}
 */
public class ConnectorUncertaintyImpact<T extends Connector> extends UncertaintyImpact<T> {
	private final AbstractPCMVertex<?> affectedElement;
	private final UncertaintySource<T> origin;
	private final PropagationHelper propagationHelper;

	/**
	 * Creates a new {@link ConnectorUncertaintyImpact} with the given affected element and origin
	 * @param affectedElement Affected element by the {@link dev.abunai.impact.analysis.model.source.ConnectorUncertaintySource}
	 * @param origin {@link dev.abunai.impact.analysis.model.source.ConnectorUncertaintySource} that caused the {@link ConnectorUncertaintyImpact}
	 * @param propagationHelper {@link PropagationHelper} used to find the affected data flows
	 */
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
		return this.affectedElement;
	}

	@Override
	public List<PCMTransposeFlowGraph> getAffectedDataFlows() {
		return propagationHelper.findTransposeFlowGraphsWithElement(this.affectedElement);
	}

}

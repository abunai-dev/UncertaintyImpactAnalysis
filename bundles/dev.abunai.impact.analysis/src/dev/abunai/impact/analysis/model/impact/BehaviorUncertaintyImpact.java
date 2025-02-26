package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents the uncertainty impact of an {@link dev.abunai.impact.analysis.model.source.BehaviorUncertaintySource}
 */
public class BehaviorUncertaintyImpact<T extends Entity> extends UncertaintyImpact<T> {
	private final AbstractPCMVertex<?> affectedElement;
	private final UncertaintySource<T> origin;
	private final PropagationHelper propagationHelper;

	/**
	 * Creates a new {@link BehaviorUncertaintyImpact} with the given affected element and origin
	 * @param affectedElement Affected element by the {@link dev.abunai.impact.analysis.model.source.BehaviorUncertaintySource}
	 * @param origin {@link dev.abunai.impact.analysis.model.source.BehaviorUncertaintySource} that caused the {@link BehaviorUncertaintyImpact}
	 * @param propagationHelper {@link PropagationHelper} used to find the affected data flows
	 */
	public BehaviorUncertaintyImpact(AbstractPCMVertex<?> affectedElement, UncertaintySource<T> origin,
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

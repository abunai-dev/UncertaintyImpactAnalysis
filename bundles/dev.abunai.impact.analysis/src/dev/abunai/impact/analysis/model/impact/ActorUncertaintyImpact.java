package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents the uncertainty impact of an {@link dev.abunai.impact.analysis.model.source.ActorUncertaintySource}
 */
public class ActorUncertaintyImpact extends UncertaintyImpact<Entity> {
	private final AbstractPCMVertex<?> affectedElement;
	private final UncertaintySource<Entity> origin;
	private final PropagationHelper propagationHelper;

	/**
	 * Creates a new {@link ActorUncertaintyImpact} with the given affected element and origin
	 * @param affectedElement Affected element by the {@link dev.abunai.impact.analysis.model.source.ActorUncertaintySource}
	 * @param origin {@link dev.abunai.impact.analysis.model.source.ActorUncertaintySource} that caused the {@link ActorUncertaintyImpact}
	 * @param propagationHelper {@link PropagationHelper} used to find the affected data flows
	 */
	public ActorUncertaintyImpact(AbstractPCMVertex<?> affectedElement, UncertaintySource<Entity> origin,
			PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<Entity> getOrigin() {
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

package dev.abunai.impact.analysis.model.impact;

import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.dataflowanalysis.analysis.pcm.core.seff.SEFFPCMVertex;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ComponentUncertaintyImpact extends UncertaintyImpact<AssemblyContext> {

	private final SEFFPCMVertex<?> affectedElement;
	private final UncertaintySource<AssemblyContext> origin;
	private final PropagationHelper propagationHelper;

	public ComponentUncertaintyImpact(SEFFPCMVertex<?> affectedElement,
									  UncertaintySource<AssemblyContext> origin, PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<AssemblyContext> getOrigin() {
		return origin;
	}

	@Override
	public SEFFPCMVertex<?> getAffectedElement() {
		return affectedElement;
	}

	@Override
	public List<PCMTransposeFlowGraph> getAffectedDataFlows() {
		return propagationHelper.findTransposeFlowGraphsWithElement(affectedElement);
	}
}

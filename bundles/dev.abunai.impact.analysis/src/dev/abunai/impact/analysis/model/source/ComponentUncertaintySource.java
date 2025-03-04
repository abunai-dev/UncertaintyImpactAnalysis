package dev.abunai.impact.analysis.model.source;

import java.util.List;
import java.util.Objects;

import org.dataflowanalysis.analysis.pcm.core.seff.SEFFPCMVertex;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import dev.abunai.impact.analysis.model.impact.ComponentUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents a source of uncertainty on an {@link AssemblyContext}
 */
public class ComponentUncertaintySource extends UncertaintySource<AssemblyContext> {
	private final AssemblyContext component;
	private final PropagationHelper propagationHelper;

	/**
	 * Create a new {@link ComponentUncertaintySource} with the given affected assembly context
	 * @param component Affected {@link AssemblyContext}
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 */
	public ComponentUncertaintySource(AssemblyContext component, PropagationHelper propagationHelper) {
		Objects.requireNonNull(component);
		Objects.requireNonNull(propagationHelper);
		this.component = component;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public AssemblyContext getArchitecturalElement() {
		return component;
	}

	@Override
	public List<ComponentUncertaintyImpact> propagate() {
		List<SEFFPCMVertex<?>> startActions = this.propagationHelper
				.findStartActionsOfAssemblyContext(this.component);
		return startActions.stream()
				.map(it -> new ComponentUncertaintyImpact(it, this, propagationHelper))
				.toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Component";
	}

}

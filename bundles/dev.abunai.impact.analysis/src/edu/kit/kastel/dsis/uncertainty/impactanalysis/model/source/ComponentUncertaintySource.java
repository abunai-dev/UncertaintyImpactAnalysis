package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.StartAction;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.ComponentUncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class ComponentUncertaintySource extends UncertaintySource<AssemblyContext> {

	private final AssemblyContext component;
	private final PropagationHelper propagationHelper;

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
		List<SEFFActionSequenceElement<StartAction>> startActions = this.propagationHelper
				.findStartActionsOfAssemblyContext(this.component);
		return startActions.stream().map(it -> new ComponentUncertaintyImpact(it, this, propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Component";
	}

}

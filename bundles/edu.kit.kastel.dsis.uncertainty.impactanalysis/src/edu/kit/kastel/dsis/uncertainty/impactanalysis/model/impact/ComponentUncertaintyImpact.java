package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.StartAction;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class ComponentUncertaintyImpact extends UncertaintyImpact<AssemblyContext> {
	
	private final SEFFActionSequenceElement<StartAction> affectedElement;
	private final UncertaintySource<AssemblyContext> origin;
	private final PropagationHelper propagationHelper;
	
	public ComponentUncertaintyImpact(SEFFActionSequenceElement<StartAction> affectedElement, UncertaintySource<AssemblyContext> origin, PropagationHelper propagationHelper) {
		this.affectedElement = affectedElement;
		this.origin = origin;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public UncertaintySource<AssemblyContext> getOrigin() {
		return origin;
	}

	@Override
	public SEFFActionSequenceElement<StartAction> getAffectedElement() {
		return affectedElement;
	}

	@Override
	public Optional<ActionSequence> getAffectedDataFlow() {
		return propagationHelper.findActionSequenceWithElement(affectedElement);
	}

	@Override
	public String toString() {
		// TODO: Can also probably be generalized
		var generalInfo = String.format("Component Uncertainty Impact on StartAction with ID %s.", this.affectedElement.getElement().getId());
		var originInfo = String.format("Origin of this impact: %s", this.getOrigin().toString());
		var affectedDataFlowInfo = String.format("Affected Data Flow: %s", this.getAffectedDataFlow().get().getElements().stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
		var emptyLine = "";
		return String.join(System.lineSeparator(), generalInfo, originInfo, affectedDataFlowInfo, emptyLine);
	}



}

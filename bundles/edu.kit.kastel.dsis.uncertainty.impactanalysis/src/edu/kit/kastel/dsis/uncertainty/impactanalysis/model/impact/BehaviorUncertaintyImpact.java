package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.entity.Entity;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class BehaviorUncertaintyImpact<T extends Entity> extends UncertaintyImpact<T> {

	private final AbstractPCMActionSequenceElement<?> affectedElement;
	private final UncertaintySource<T> origin;
	private final PropagationHelper propagationHelper;

	public BehaviorUncertaintyImpact(AbstractPCMActionSequenceElement<?> affectedElement, UncertaintySource<T> origin,
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
	public Optional<ActionSequence> getAffectedDataFlow() {
		// TODO: Can maybe be generalized?
		return propagationHelper.findActionSequenceWithElement(affectedElement);
	}

	@Override
	public String toString() {
		// TODO: Generalize
		var generalInfo = String.format("Behavior Uncertainty Impact on action with ID %s.",
				EcoreUtil.getID(this.affectedElement.getElement()));
		var originInfo = String.format("Origin of this impact: %s", this.getOrigin().toString());
		var affectedDataFlowInfo = String.format("Affected Data Flow: %s", this.getAffectedDataFlow().get()
				.getElements().stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
		var emptyLine = "";
		return String.join(System.lineSeparator(), generalInfo, originInfo, affectedDataFlowInfo, emptyLine);
	}

}

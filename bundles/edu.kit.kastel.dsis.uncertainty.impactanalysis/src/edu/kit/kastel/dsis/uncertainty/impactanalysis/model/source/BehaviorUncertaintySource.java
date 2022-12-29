package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.BehaviorUncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.UncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class BehaviorUncertaintySource<T extends Entity> extends UncertaintySource<T> {

	private final T action;
	private final PropagationHelper propagationHelper;

	public BehaviorUncertaintySource(T action, PropagationHelper propagationHelper) {
		Objects.requireNonNull(action);
		Objects.requireNonNull(propagationHelper);
		this.action = action;
		this.propagationHelper = propagationHelper;
	}

	public static BehaviorUncertaintySource<?> of(Entity action, PropagationHelper propagationHelper) {
		if (action instanceof EntryLevelSystemCall) {
			return new BehaviorUncertaintySource<EntryLevelSystemCall>((EntryLevelSystemCall) action,
					propagationHelper);
		} else if (action instanceof ExternalCallAction) {
			return new BehaviorUncertaintySource<ExternalCallAction>((ExternalCallAction) action, propagationHelper);
		} else if (action instanceof SetVariableAction) {
			return new BehaviorUncertaintySource<SetVariableAction>((SetVariableAction) action, propagationHelper);
		} else {
			throw new IllegalStateException("Unrecognized action type.");
		}
	}

	@Override
	public T getArchitecturalElement() {
		return this.action;
	}

	@Override
	public List<? extends UncertaintyImpact<T>> propagate() {
		List<AbstractPCMActionSequenceElement<?>> processes = this.propagationHelper.findProccessesWithAction(action);
		return processes.stream().map(it -> new BehaviorUncertaintyImpact<>(it, this, this.propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Behavior";
	}

}

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
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.ActionType;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class BehaviorUncertaintySource<T extends Entity> extends UncertaintySource<T> {

	private final T action;
	private final ActionType actionType;
	private final PropagationHelper propagationHelper;

	public BehaviorUncertaintySource(T action, ActionType actionType, PropagationHelper propagationHelper) {
		Objects.requireNonNull(action);
		Objects.requireNonNull(actionType);
		Objects.requireNonNull(propagationHelper);
		this.action = action;
		this.actionType = actionType;
		this.propagationHelper = propagationHelper;
	}

	public static BehaviorUncertaintySource<?> of(Entity action, ActionType actionType,
			PropagationHelper propagationHelper) {
		switch (actionType) {
		case ENTRY_LEVEL_SYSTEM_CALL:
			return new BehaviorUncertaintySource<EntryLevelSystemCall>((EntryLevelSystemCall) action, actionType,
					propagationHelper);
		case EXTERNAL_CALL_ACTION:
			return new BehaviorUncertaintySource<ExternalCallAction>((ExternalCallAction) action, actionType,
					propagationHelper);
		case SET_VARIABLE_ACTION:
			return new BehaviorUncertaintySource<SetVariableAction>((SetVariableAction) action, actionType,
					propagationHelper);
		default:
			throw new IllegalStateException("Unrecognized action type.");
		}
	}

	@Override
	public T getArchitecturalElement() {
		return this.action;
	}

	@Override
	public List<? extends UncertaintyImpact<T>> propagate() {
		List<? extends AbstractPCMActionSequenceElement<?>> processes = this.propagationHelper
				.findProccessesWithActionOfType(action, actionType);
		return processes.stream().map(it -> new BehaviorUncertaintyImpact<>(it, this, this.propagationHelper)).toList();
	}

	@Override
	public String toString() {
		return String.format("Behavior Uncertainty annotated to action \"%s\" (%s) of type %s.",
				this.getArchitecturalElement().getEntityName(), this.getArchitecturalElement().getId(),
				this.actionType.toString());
	}

}

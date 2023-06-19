package dev.abunai.impact.analysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

import dev.abunai.impact.analysis.model.impact.BehaviorUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class BehaviorUncertaintySource<T extends Entity> extends UncertaintySource<T> {

	private final T action;
	private final PropagationHelper propagationHelper;

	private BehaviorUncertaintySource(T action, PropagationHelper propagationHelper) {
		Objects.requireNonNull(action);
		Objects.requireNonNull(propagationHelper);
		this.action = action;
		this.propagationHelper = propagationHelper;
	}

	public static BehaviorUncertaintySource<?> of(Entity action, PropagationHelper propagationHelper) {
		if (action instanceof EntryLevelSystemCall call) {
			return new BehaviorUncertaintySource<EntryLevelSystemCall>(call, propagationHelper);
		} else if (action instanceof ExternalCallAction call) {
			return new BehaviorUncertaintySource<ExternalCallAction>(call, propagationHelper);
		} else if (action instanceof SetVariableAction variableAction) {
			return new BehaviorUncertaintySource<SetVariableAction>(variableAction, propagationHelper);
		} else if (action instanceof StartAction branchStartAction) {
			return new BehaviorUncertaintySource<StartAction>(branchStartAction, propagationHelper);
		} else {
			throw new IllegalStateException("Unsupported action type.");
		}
	}

	@Override
	public T getArchitecturalElement() {
		return this.action;
	}

	@Override
	public List<BehaviorUncertaintyImpact<T>> propagate() {
		List<AbstractPCMActionSequenceElement<?>> processes = this.propagationHelper.findProccessesWithAction(action);
		return processes.stream().map(it -> new BehaviorUncertaintyImpact<>(it, this, this.propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Behavior";
	}

}

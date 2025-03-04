package dev.abunai.impact.analysis.model.source;

import java.util.List;
import java.util.Objects;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

import dev.abunai.impact.analysis.model.impact.BehaviorUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents a source of uncertainty on an {@link EntryLevelSystemCall}, {@link ExternalCallAction}, {@link SetVariableAction} or {@link StartAction}
 * @param <T> Type of the element affected by uncertainty
 */
public class BehaviorUncertaintySource<T extends Entity> extends UncertaintySource<T> {
	private final T action;
	private final PropagationHelper propagationHelper;

	/**
	 * Create a new {@link BehaviorUncertaintySource} with the given affected action element
	 * @param action Affected action {@link Entity} object
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 */
	private BehaviorUncertaintySource(T action, PropagationHelper propagationHelper) {
		Objects.requireNonNull(action);
		Objects.requireNonNull(propagationHelper);
		this.action = action;
		this.propagationHelper = propagationHelper;
	}

	/**
	 * Create a new {@link BehaviorUncertaintySource} with the given affected action element
	 * @param action Affected action {@link Entity} object
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 * @return Returns a new {@link BehaviorUncertaintySource} with the given action {@link Entity}
	 */
	public static BehaviorUncertaintySource<?> of(Entity action, PropagationHelper propagationHelper) {
		if (action instanceof EntryLevelSystemCall call) {
			return new BehaviorUncertaintySource<>(call, propagationHelper);
		} else if (action instanceof ExternalCallAction call) {
			return new BehaviorUncertaintySource<>(call, propagationHelper);
		} else if (action instanceof SetVariableAction variableAction) {
			return new BehaviorUncertaintySource<>(variableAction, propagationHelper);
		} else if (action instanceof StartAction branchStartAction) {
			return new BehaviorUncertaintySource<>(branchStartAction, propagationHelper);
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
		List<AbstractPCMVertex<?>> processes = this.propagationHelper.findProcessesWithAction(action);
		return processes.stream()
				.map(it -> new BehaviorUncertaintyImpact<>(it, this, this.propagationHelper))
				.toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Behavior";
	}
}

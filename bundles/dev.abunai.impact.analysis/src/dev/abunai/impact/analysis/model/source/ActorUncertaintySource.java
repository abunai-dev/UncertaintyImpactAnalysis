package dev.abunai.impact.analysis.model.source;

import java.util.List;
import java.util.Objects;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dev.abunai.impact.analysis.model.impact.ActorUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents a source of uncertainty on an {@link UsageScenario} or {@link ResourceContainer}
 * @param <T> Type of the element affected by uncertainty
 */
public class ActorUncertaintySource<T extends Entity> extends UncertaintySource<Entity> {
	private final T actor;
	private final PropagationHelper propagationHelper;

	/**
	 * Create a new {@link ActorUncertaintySource} with the given affected actor element
	 * @param actor Affected actor {@link Entity} object
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 */
	private ActorUncertaintySource(T actor, PropagationHelper propagationHelper) {
		Objects.requireNonNull(actor);
		Objects.requireNonNull(propagationHelper);
		this.actor = actor;
		this.propagationHelper = propagationHelper;
	}

	/**
	 * Create a new {@link ActorUncertaintySource} with the given affected actor element
	 * @param actor Affected actor {@link Entity} object
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 * @return Returns a new {@link ActorUncertaintySource} with the given actor {@link Entity}
	 */
	public static ActorUncertaintySource<?> of(Entity actor, PropagationHelper propagationHelper) {
		if (actor instanceof UsageScenario usageScenario) {
			return new ActorUncertaintySource<>(usageScenario, propagationHelper);
		} else if (actor instanceof ResourceContainer resourceContainer) {
			return new ActorUncertaintySource<>(resourceContainer, propagationHelper);
		} else {
			throw new IllegalStateException("Unsupported actor type");
		}
	}

	@Override
	public T getArchitecturalElement() {
		return this.actor;
	}

	@Override
	public List<ActorUncertaintyImpact> propagate() {
		List<? extends AbstractPCMVertex<?>> processes = this.propagationHelper
				.findProcessesThatRepresentResourceContainerOrUsageScenario(this.actor);
		return processes.stream()
				.map(it -> new ActorUncertaintyImpact(it, this, this.propagationHelper))
				.toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Actor";
	}

}

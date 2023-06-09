package dev.abunai.impact.analysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dev.abunai.impact.analysis.model.impact.ActorUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ActorUncertaintySource<T extends Entity> extends UncertaintySource<Entity> {

	private final T actor;
	private final PropagationHelper propagationHelper;

	private ActorUncertaintySource(T actor, PropagationHelper propagationHelper) {
		Objects.requireNonNull(actor);
		Objects.requireNonNull(propagationHelper);
		this.actor = actor;
		this.propagationHelper = propagationHelper;
	}

	public static ActorUncertaintySource<?> of(Entity actor, PropagationHelper propagationHelper) {
		if (actor instanceof UsageScenario usageScenario) {
			return new ActorUncertaintySource<UsageScenario>(usageScenario, propagationHelper);
		} else if (actor instanceof ResourceContainer resourceContainer) {
			return new ActorUncertaintySource<ResourceContainer>(resourceContainer, propagationHelper);
		} else {
			throw new IllegalStateException("Unsupported actor type.");
		}
	}

	@Override
	public T getArchitecturalElement() {
		return this.actor;
	}

	@Override
	public List<ActorUncertaintyImpact> propagate() {
		List<? extends AbstractPCMActionSequenceElement<?>> processes = propagationHelper
				.findProcessesThatRepresentResourceContainerOrUsageScenario(this.actor);
		return processes.stream().map(it -> new ActorUncertaintyImpact(it, this, this.propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Actor";
	}

}

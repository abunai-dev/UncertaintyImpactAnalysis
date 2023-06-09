package dev.abunai.impact.analysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.impact.ActorUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class ActorUncertaintySource extends UncertaintySource<Entity> {

	private final Entity actor;
	private final PropagationHelper propagationHelper;

	public ActorUncertaintySource(Entity actor, PropagationHelper propagationHelper) {
		Objects.requireNonNull(actor);
		Objects.requireNonNull(propagationHelper);
		this.actor = actor;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public Entity getArchitecturalElement() {
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

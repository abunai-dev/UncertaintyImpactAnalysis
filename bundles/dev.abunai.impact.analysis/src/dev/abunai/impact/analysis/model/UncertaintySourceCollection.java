package dev.abunai.impact.analysis.model;

import java.util.ArrayList;
import java.util.List;

import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;
import dev.abunai.impact.analysis.model.source.ActorUncertaintySource;
import dev.abunai.impact.analysis.model.source.BehaviorUncertaintySource;
import dev.abunai.impact.analysis.model.source.ComponentUncertaintySource;
import dev.abunai.impact.analysis.model.source.ConnectorUncertaintySource;
import dev.abunai.impact.analysis.model.source.InterfaceUncertaintySource;
import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class UncertaintySourceCollection {
	private final List<UncertaintySource<?>> uncertaintySources;
	private final PropagationHelper propagationHelper;
	private final PCMFlowGraphCollection flowGraphs;

	public UncertaintySourceCollection(PCMFlowGraphCollection flowGraphs, PropagationHelper propagationHelper) {
		this.uncertaintySources = new ArrayList<>();
		this.propagationHelper = propagationHelper;
		this.flowGraphs = flowGraphs;
	}

	public UncertaintyImpactCollection propagate() {
		List<UncertaintyImpact<?>> allImpacts = new ArrayList<>();
		for (UncertaintySource<?> source : this.uncertaintySources) {
			var localImpacts = source.propagate();
            allImpacts.addAll(localImpacts);
		}
		return new UncertaintyImpactCollection(flowGraphs, allImpacts);
	}

	public void addComponentUncertaintyInAssemblyContext(String id) {
		var component = this.propagationHelper.findAssemblyContext(id);
		if (component.isEmpty()) {
			throw new IllegalArgumentException("Unable to find an assembly context with the given ID.");
		} else {
			this.uncertaintySources.add(new ComponentUncertaintySource(component.get(), propagationHelper));
		}

	}

	public void addBehaviorUncertaintyInEntryLevelSystemCall(String id) {
		this.addBehaviorUncertainty(id, EntryLevelSystemCall.class);
	}

	public void addBehaviorUncertaintyInExternalCallAction(String id) {
		this.addBehaviorUncertainty(id, ExternalCallAction.class);
	}

	public void addBehaviorUncertaintyInSetVariableAction(String id) {
		this.addBehaviorUncertainty(id, SetVariableAction.class);
	}

	public void addBehaviorUncertaintyInBranch(String id) {
		var startActions = this.propagationHelper.findStartActionsOfBranchAction(id);
		if (startActions.isEmpty()) {
			throw new IllegalArgumentException("Unable to find start actions of the branch action with the given id.");
		}
		for (var action : startActions) {
			addBehaviorUncertainty(action.getId(), StartAction.class);
		}
	}

	private void addBehaviorUncertainty(String id, Class<? extends Entity> targetType) {
		var action = this.propagationHelper.findAction(id);
		if (action.isEmpty() || !targetType.isInstance(action.get())) {
			throw new IllegalArgumentException(
					String.format("Unable to find %s with the given id.", targetType.getSimpleName()));
		}
		this.uncertaintySources.add(BehaviorUncertaintySource.of(targetType.cast(action.get()), propagationHelper));
	}

	public void addActorUncertaintyInResourceContainer(String id) {
		var actor = this.propagationHelper.findResourceContainer(id);
		if (actor.isEmpty()) {
			throw new IllegalArgumentException("Unable to find resource container with the given ID.");
		}
		this.uncertaintySources.add(ActorUncertaintySource.of(actor.get(), propagationHelper));
	}

	public void addActorUncertaintyInUsageScenario(String id) {
		var actor = this.propagationHelper.findUsageScenario(id);
		if (actor.isEmpty()) {
			throw new IllegalArgumentException("Unable to find usage scenario with the given ID.");
		}
		this.uncertaintySources.add(ActorUncertaintySource.of(actor.get(), propagationHelper));
	}

	public void addInterfaceUncertaintyInSignature(String id) {
		var signature = this.propagationHelper.findSignature(id);
		if (signature.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the signature with the given ID.");
		}
		this.uncertaintySources.add(new InterfaceUncertaintySource(signature.get(), propagationHelper));
	}

	public void addInterfaceUncertaintyInInterface(String id) {
		var interfaze = this.propagationHelper.findInterface(id);
		if (interfaze.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the interface with the given ID.");
		}
		for (var signature : interfaze.get().getSignatures__OperationInterface()) {
			this.addInterfaceUncertaintyInSignature(signature.getId());
		}
	}

	public void addConnectorUncertaintyInConnector(String id) {
		var connector = this.propagationHelper.findConnector(id);
		if (connector.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the connector with the given ID.");
		}
		this.uncertaintySources.add(ConnectorUncertaintySource.of(connector.get(), propagationHelper));
	}
}

package dev.abunai.impact.analysis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
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
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

/**
 * Contains a collection of {@link UncertaintySource} elements affecting a {@link PCMFlowGraphCollection}
 */
public class UncertaintySourceCollection {
	private final List<UncertaintySource<?>> uncertaintySources;
	private final PropagationHelper propagationHelper;
	private final PCMFlowGraphCollection flowGraphs;

	/**
	 * Creates a new {@link UncertaintySourceCollection} with the given affected {@link PCMFlowGraphCollection}
	 * @param flowGraphs {@link PCMFlowGraphCollection} containing the affected {@link org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph}
	 * @param propagationHelper {@link PropagationHelper} that is used to determine affected elements and transpose flow graphs
	 */
	public UncertaintySourceCollection(PCMFlowGraphCollection flowGraphs, PropagationHelper propagationHelper) {
		this.uncertaintySources = new ArrayList<>();
		this.propagationHelper = propagationHelper;
		this.flowGraphs = flowGraphs;
	}

	/**
	 * Propagates the contained {@link UncertaintySource} to a collection of {@link UncertaintyImpact} elements.
	 * This operation results in an {@link UncertaintyImpactCollection} containing the results
	 * @return Returns a {@link UncertaintyImpactCollection} containing the propagated {@link UncertaintyImpact} elements
	 */
	public UncertaintyImpactCollection propagate() {
		List<UncertaintyImpact<?>> allImpacts = new ArrayList<>();
		for (UncertaintySource<?> source : this.uncertaintySources) {
			var localImpacts = source.propagate();
            allImpacts.addAll(localImpacts);
		}
		return new UncertaintyImpactCollection(flowGraphs, allImpacts);
	}

	/**
	 * Affects the element with the given id with an {@link ComponentUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addComponentUncertaintyInAssemblyContext(String id) {
		var component = this.propagationHelper.findAssemblyContext(id);
		if (component.isEmpty()) {
			throw new IllegalArgumentException("Unable to find an assembly context with the given ID.");
		} else {
			this.uncertaintySources.add(new ComponentUncertaintySource(component.get(), propagationHelper));
		}

	}

	/**
	 * Affects the element, a {@link EntryLevelSystemCall}, with the given id with an {@link BehaviorUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addBehaviorUncertaintyInEntryLevelSystemCall(String id) {
		this.addBehaviorUncertainty(id, EntryLevelSystemCall.class);
	}

	/**
	 * Affects the element, a {@link ExternalCallAction}, with the given id with an {@link BehaviorUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addBehaviorUncertaintyInExternalCallAction(String id) {
		this.addBehaviorUncertainty(id, ExternalCallAction.class);
	}

	/**
	 * Affects the element, a {@link SetVariableAction}, with the given id with an {@link BehaviorUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addBehaviorUncertaintyInSetVariableAction(String id) {
		this.addBehaviorUncertainty(id, SetVariableAction.class);
	}

	/**
	 * Affects the elements, each {@link StartAction} of an branch, with the given id with an {@link BehaviorUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addBehaviorUncertaintyInBranch(String id) {
		var startActions = this.propagationHelper.findStartActionsOfBranchAction(id);
		if (startActions.isEmpty()) {
			throw new IllegalArgumentException("Unable to find start actions of the branch action with the given id.");
		}
		for (var action : startActions) {
			addBehaviorUncertainty(action.getId(), StartAction.class);
		}
	}

	/**
	 * Affects the element with the given id with an {@link BehaviorUncertaintySource}
	 * @param id ID of the affected element
	 * @param targetType Targeted type to which the behavior uncertainty is added
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	private void addBehaviorUncertainty(String id, Class<? extends Entity> targetType) {
		var action = this.propagationHelper.findAction(id);
		if (action.isEmpty() || !targetType.isInstance(action.get())) {
			throw new IllegalArgumentException(
					String.format("Unable to find %s with the given id.", targetType.getSimpleName()));
		}
		this.uncertaintySources.add(BehaviorUncertaintySource.of(targetType.cast(action.get()), propagationHelper));
	}

	/**
	 * Affects the element, a {@link ResourceContainer} with the given id with an {@link ActorUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addActorUncertaintyInResourceContainer(String id) {
		Optional<ResourceContainer> actor = this.propagationHelper.findResourceContainer(id);
		if (actor.isEmpty()) {
			throw new IllegalArgumentException("Unable to find resource container with the given ID.");
		}
		this.uncertaintySources.add(ActorUncertaintySource.of(actor.get(), propagationHelper));
	}

	/**
	 * Affects the element, a {@link UsageScenario} with the given id with an {@link ActorUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addActorUncertaintyInUsageScenario(String id) {
		Optional<UsageScenario> actor = this.propagationHelper.findUsageScenario(id);
		if (actor.isEmpty()) {
			throw new IllegalArgumentException("Unable to find usage scenario with the given ID.");
		}
		this.uncertaintySources.add(ActorUncertaintySource.of(actor.get(), propagationHelper));
	}

	/**
	 * Affects the element, a {@link OperationSignature}< with the given id with an {@link InterfaceUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addInterfaceUncertaintyInSignature(String id) {
		Optional<OperationSignature> signature = this.propagationHelper.findSignature(id);
		if (signature.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the signature with the given ID.");
		}
		this.uncertaintySources.add(new InterfaceUncertaintySource(signature.get(), propagationHelper));
	}

	/**
	 * Affects the element, a {@link OperationInterface}, with the given id with an {@link ComponentUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addInterfaceUncertaintyInInterface(String id) {
		Optional<OperationInterface> interfaze = this.propagationHelper.findInterface(id);
		if (interfaze.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the interface with the given ID.");
		}
		for (var signature : interfaze.get().getSignatures__OperationInterface()) {
			this.addInterfaceUncertaintyInSignature(signature.getId());
		}
	}

	/**
	 * Affects the element, a {@link Connector}, with the given id with an {@link ConnectorUncertaintySource}
	 * @param id ID of the affected element
	 * @throws IllegalArgumentException Thrown if no fitting element with the given id can be found
	 */
	public void addConnectorUncertaintyInConnector(String id) {
		Optional<Connector> connector = this.propagationHelper.findConnector(id);
		if (connector.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the connector with the given ID.");
		}
		this.uncertaintySources.add(ConnectorUncertaintySource.of(connector.get(), propagationHelper));
	}
}

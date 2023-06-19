package dev.abunai.impact.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.AnalysisData;
import org.palladiosimulator.dataflow.confidentiality.analysis.core.AbstractStandalonePCMDataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
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

public class StandalonePCMUncertaintyImpactAnalysis extends AbstractStandalonePCMDataFlowConfidentialityAnalysis {

	private final AnalysisData analysisData;
	private List<UncertaintySource<?>> uncertaintySources = new ArrayList<>();

	private List<ActionSequence> actionSequences = null;
	private PropagationHelper propagationHelper = null;

	public StandalonePCMUncertaintyImpactAnalysis(String modelProjectName, Class<? extends Plugin> pluginActivator,
			AnalysisData analysisData) {
		super(analysisData, Logger.getLogger(StandalonePCMUncertaintyImpactAnalysis.class), modelProjectName,
				pluginActivator);

		this.analysisData = analysisData;
	}

	@Override
	public boolean setupAnalysis() {
		return true;
	}

	@Override
	public boolean initializeAnalysis() {
		if (super.initializeAnalysis()) {
			this.actionSequences = super.findAllSequences();
			this.propagationHelper = new PropagationHelper(this.actionSequences, analysisData.getResourceLoader());
			return true;
		} else {
			return false;
		}
	}

	public List<UncertaintyImpact<?>> propagate() {
		List<UncertaintyImpact<?>> allImpacts = new ArrayList<>();

		for (UncertaintySource<?> source : this.uncertaintySources) {
			var localImpacts = source.propagate();

			for (var impact : localImpacts) {
				allImpacts.add(impact);
			}
		}

		return allImpacts;
	}

	public List<AbstractPCMActionSequenceElement<?>> getAllAffectedElementsAfterPropagation() {
		List<UncertaintyImpact<?>> allImpacts = this.propagate();
		return allImpacts.stream().map(it -> it.getAffectedElement()).collect(Collectors.toList());
	}

	public List<ActionSequence> getAllAffectedDataFlowSectionsAfterPropagation() {
		List<UncertaintyImpact<?>> allImpacts = this.propagate();
		return allImpacts.stream().map(it -> it.getAffectedDataFlowSections()).flatMap(Collection::stream).toList();
	}

	public Set<ActionSequence> getImpactSet(boolean distinct) {
		List<ActionSequence> allAffectedSequences = this.getAllAffectedDataFlowSectionsAfterPropagation();

		Set<ActionSequence> impactSet = new HashSet<ActionSequence>();
		for (ActionSequence actionSequence : allAffectedSequences) {
			if (impactSet.stream().anyMatch(it -> {
				var otherNodes = it.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast).toList();
				var ownNodes = actionSequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
						.toList();

				var otherPCMElements = otherNodes.stream().map(AbstractPCMActionSequenceElement::getElement).toList();
				var ownPCMElements = ownNodes.stream().map(AbstractPCMActionSequenceElement::getElement).toList();

				return otherPCMElements.equals(ownPCMElements);
			})) {
				continue;
			} else {
				impactSet.add(actionSequence);
			}
		}

		if (distinct) {
			Set<ActionSequence> entriesToRemove = new HashSet<ActionSequence>();
			for (ActionSequence actionSequence : impactSet) {
				List<ActionSequence> similarDataFlows = impactSet.stream().filter(it -> getActionSequenceIndex(
						it.getElements()) == getActionSequenceIndex(actionSequence.getElements())).toList();

				for (ActionSequence similarDataFlow : similarDataFlows) {
					if (similarDataFlow.equals(actionSequence)) {
						continue;
					} else if (actionSequence.getElements().size() >= similarDataFlow.getElements().size()) {
						entriesToRemove.add(similarDataFlow);
					} else {
						entriesToRemove.add(actionSequence);
					}
				}
			}
			impactSet.removeAll(entriesToRemove);
		}

		return impactSet;
	}

	public int getActionSequenceIndex(List<AbstractActionSequenceElement<?>> entries) {
		for (int i = 0; i < this.actionSequences.size(); i++) {
			var elements = this.actionSequences.get(i).getElements().stream()
					.map(AbstractPCMActionSequenceElement.class::cast).toList();

			if (Collections.indexOfSubList(elements, entries) != -1) {
				return i;
			}
		}

		return -1;
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
		addBehaviorUncertainty(id, EntryLevelSystemCall.class);
	}

	public void addBehaviorUncertaintyInExternalCallAction(String id) {
		addBehaviorUncertainty(id, ExternalCallAction.class);
	}

	public void addBehaviorUncertaintyInSetVariableAction(String id) {
		addBehaviorUncertainty(id, SetVariableAction.class);
	}

	public void addBehaviorUncertaintyInBranch(String id) {
		// Special case: Map BranchAction to all contained StartActions first
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

		if (action.isPresent() && targetType.isInstance(action.get())) {
			this.uncertaintySources.add(BehaviorUncertaintySource.of(targetType.cast(action.get()), propagationHelper));
		} else {
			throw new IllegalArgumentException(
					String.format("Unable to find %s with the given id.", targetType.getSimpleName()));
		}
	}

	public void addActorUncertaintyInResourceContainer(String id) {
		var actor = this.propagationHelper.findResourceContainer(id);

		if (actor.isEmpty()) {
			throw new IllegalArgumentException("Unable to find resource container with the given ID.");
		} else {
			this.uncertaintySources.add(ActorUncertaintySource.of(actor.get(), propagationHelper));
		}
	}

	public void addActorUncertaintyInUsageScenario(String id) {
		var actor = this.propagationHelper.findUsageScenario(id);

		if (actor.isEmpty()) {
			throw new IllegalArgumentException("Unable to find usage scenario with the given ID.");
		} else {
			this.uncertaintySources.add(ActorUncertaintySource.of(actor.get(), propagationHelper));
		}
	}

	public void addInterfaceUncertaintyInSignature(String id) {
		var signature = this.propagationHelper.findSignature(id);

		if (signature.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the signature with the given ID.");
		} else {
			this.uncertaintySources.add(new InterfaceUncertaintySource(signature.get(), propagationHelper));
		}
	}

	public void addInterfaceUncertaintyInInterface(String id) {
		var interfaze = this.propagationHelper.findInterface(id);

		if (interfaze.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the interface with the given ID.");
		} else {
			for (var signature : interfaze.get().getSignatures__OperationInterface()) {
				this.addInterfaceUncertaintyInSignature(signature.getId());
			}
		}
	}

	public void addConnectorUncertaintyInConnector(String id) {
		var connector = this.propagationHelper.findConnector(id);

		if (connector.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the connector with the given ID.");
		} else {
			this.uncertaintySources.add(ConnectorUncertaintySource.of(connector.get(), propagationHelper));
		}
	}

}

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
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;
import dev.abunai.impact.analysis.model.source.ActorUncertaintySource;
import dev.abunai.impact.analysis.model.source.BehaviorUncertaintySource;
import dev.abunai.impact.analysis.model.source.ComponentUncertaintySource;
import dev.abunai.impact.analysis.model.source.ConnectorUncertaintySource;
import dev.abunai.impact.analysis.model.source.InterfaceUncertaintySource;
import dev.abunai.impact.analysis.model.source.UncertaintySource;
import dev.abunai.impact.analysis.util.PropagationHelper;

// FIXME: Fix inheritance, use builder pattern
public class StandalonePCMUncertaintyImpactAnalysis extends AbstractStandalonePCMDataFlowConfidentialityAnalysis {

	private final AnalysisData analysisData;
	private List<ActionSequence> actionSequences = null;
	private PropagationHelper propagationHelper = null;
	private List<UncertaintySource<?>> uncertaintySources = new ArrayList<>();
	
	public StandalonePCMUncertaintyImpactAnalysis(String modelProjectName, Class<? extends Plugin> pluginActivator, AnalysisData analysisData) {
		super(analysisData,
				Logger.getLogger(StandalonePCMUncertaintyImpactAnalysis.class),
				modelProjectName, pluginActivator);
		
		this.analysisData = analysisData;
    }
	
	@Override
	public boolean setupAnalysis() {
		return true;
	}

	@Override
	public boolean initializeAnalysis() {
		boolean initSuccessful = super.initializeAnalysis();
		
		this.actionSequences = super.findAllSequences();
		this.propagationHelper = new PropagationHelper(this.actionSequences, analysisData.getResourceLoader());
		return initSuccessful;
	}
	
	private Repository getRepositoryModel() {
		return this.getSystemModel().getAssemblyContexts__ComposedStructure().get(0).getEncapsulatedComponent__AssemblyContext().getRepository__RepositoryComponent();
	}
	
	private System getSystemModel() {
		return analysisData.getResourceLoader().getAllocation().getSystem_Allocation();
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
		
		if(distinct) {
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

	public void addComponentUncertainty(String id) {
		var component = this.propagationHelper.findAssemblyContext(id);

		if (component.isEmpty()) {
			throw new IllegalArgumentException("Unable to find an assembly context with the given ID.");
		} else {
			this.uncertaintySources.add(new ComponentUncertaintySource(component.get(), propagationHelper));
		}

	}

	public void addBehaviorUncertainty(String id) {
		var action = this.propagationHelper.findAction(id);

		if (action.isEmpty()) {
			throw new IllegalArgumentException("Unable to find an action with the given ID.");
		} else {
			this.uncertaintySources.add(BehaviorUncertaintySource.of(action.get(), propagationHelper));
		}
	}

	public void addActorUncertainty(String id) {
		var annotation = this.propagationHelper.findEnumCharacteristicAnnotation(id);

		if (annotation.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the characteristic annotation with the given ID.");
		} else {
			this.uncertaintySources.add(new ActorUncertaintySource(annotation.get(), propagationHelper));
		}
	}

	public void addInterfaceUncertainty(String id) {
		var interfaze = this.propagationHelper.findInterface(id, this.getRepositoryModel());

		if (interfaze.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the interface with the given ID.");
		} else {
			this.uncertaintySources.add(new InterfaceUncertaintySource(interfaze.get(), propagationHelper));
		}
	}

	public void addConnectorUncertainty(String id) {
		var connector = this.propagationHelper.findConnector(id, this.getSystemModel());

		if (connector.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the connector with the given ID.");
		} else {
			this.uncertaintySources.add(ConnectorUncertaintySource.of(connector.get(), propagationHelper));
		}
	}

}

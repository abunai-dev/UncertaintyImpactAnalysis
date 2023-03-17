package edu.kit.kastel.dsis.uncertainty.impactanalysis;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.dataflow.confidentiality.analysis.PCMAnalysisUtils;
import org.palladiosimulator.dataflow.confidentiality.analysis.StandalonePCMDataFlowConfidentialtyAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.PCMActionSequence;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.UncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ActorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.BehaviorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ComponentUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ConnectorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.InterfaceUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class StandalonePCMUncertaintyImpactAnalysis extends StandalonePCMDataFlowConfidentialtyAnalysis {

	private final URI repositoryModelURI;
	private Repository repositoryModel;

	private final URI systemModleURI;
	private System systemModel;

	private final String modelProjectName;

	private List<ActionSequence> actionSequences = null;
	private PropagationHelper propagationHelper = null;
	private List<UncertaintySource<?>> uncertaintySources = new ArrayList<>();

	public StandalonePCMUncertaintyImpactAnalysis(String modelProjectName,
			Class<? extends Plugin> modelProjectActivator, String relativeUsageModelPath,
			String relativeAllocationModelPath, String relativeRepositoryModelPath, String relativeSystemModelPath) {

		super(modelProjectName, modelProjectActivator, relativeUsageModelPath, relativeAllocationModelPath);
		this.modelProjectName = modelProjectName;

		this.repositoryModelURI = createRelativePluginURI(relativeRepositoryModelPath);
		this.systemModleURI = createRelativePluginURI(relativeSystemModelPath);
	}

	private URI createRelativePluginURI(String relativePath) {
		String path = Paths.get(this.modelProjectName, relativePath).toString();
		return URI.createPlatformPluginURI(path, false);
	}

	@Override
	public boolean initalizeAnalysis() {
		boolean initSuccessful = super.initalizeAnalysis();

		try {
			this.repositoryModel = (Repository) PCMAnalysisUtils.loadModelContent(repositoryModelURI);
			this.systemModel = (System) PCMAnalysisUtils.loadModelContent(systemModleURI);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}

		this.actionSequences = super.findAllSequences();
		this.propagationHelper = new PropagationHelper(this.actionSequences);
		return initSuccessful;
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
		
		// TODO: Rewrite using iterator, move to separate method
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
		var interfaze = this.propagationHelper.findInterface(id, this.repositoryModel);

		if (interfaze.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the interface with the given ID.");
		} else {
			this.uncertaintySources.add(new InterfaceUncertaintySource(interfaze.get(), propagationHelper));
		}
	}

	public void addConnectorUncertainty(String id) {
		var connector = this.propagationHelper.findConnector(id, this.systemModel);

		if (connector.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the connector with the given ID.");
		} else {
			this.uncertaintySources.add(ConnectorUncertaintySource.of(connector.get(), propagationHelper));
		}
	}

}

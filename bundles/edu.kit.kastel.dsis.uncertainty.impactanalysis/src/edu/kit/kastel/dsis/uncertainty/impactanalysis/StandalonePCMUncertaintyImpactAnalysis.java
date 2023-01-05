package edu.kit.kastel.dsis.uncertainty.impactanalysis;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.dataflow.confidentiality.analysis.PCMAnalysisUtils;
import org.palladiosimulator.dataflow.confidentiality.analysis.StandalonePCMDataFlowConfidentialtyAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
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

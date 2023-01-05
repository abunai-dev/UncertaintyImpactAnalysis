package edu.kit.kastel.dsis.uncertainty.impactanalysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.StandalonePCMDataFlowConfidentialtyAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.UncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ActorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.BehaviorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ComponentUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ConnectorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.InterfaceUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;
import org.palladiosimulator.pcm.system.System;

public class StandalonePCMUncertaintyImpactAnalysis extends StandalonePCMDataFlowConfidentialtyAnalysis {

	public StandalonePCMUncertaintyImpactAnalysis(String modelProjectName,
			Class<? extends Plugin> modelProjectActivator, String relativeUsageModelPath,
			String relativeAllocationModelPath) {
		super(modelProjectName, modelProjectActivator, relativeUsageModelPath, relativeAllocationModelPath);
	}

	private List<ActionSequence> actionSequences = null;
	private PropagationHelper propagationHelper = null;
	private List<UncertaintySource<?>> uncertaintySources = new ArrayList<>();

	@Override
	public boolean initalizeAnalysis() {
		boolean initSuccessful = super.initalizeAnalysis();
		this.actionSequences = super.findAllSequences();
		this.propagationHelper = new PropagationHelper(this.actionSequences);
		return initSuccessful;
	}

	private Repository getRepository() {
		// TODO: Simple fix for now. Retrieving some SEFF element, looking up the
		// repository above. This works under assumptions regarding the nesting of the
		// first found SEFF Element (must be directly in the SEFF, must be contained in
		// a simple Component)
		SEFFActionSequenceElement<? extends AbstractAction> someSEFFElement = (SEFFActionSequenceElement<?>) super.findAllSequences()
				.get(0).getElements().stream().filter(SEFFActionSequenceElement.class::isInstance).findFirst().get();
		RepositoryComponent component = (RepositoryComponent) someSEFFElement.getElement().eContainer().eContainer();
		Repository repository = component.getRepository__RepositoryComponent();
		return repository;
	}

	private System getSystem() {
		// TODO: Simple fix for now. Only works with the symbol model, composite
		// components are ignored.
		return null;
		// TODO: Try loading models directly
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
		var interfaze = this.propagationHelper.findInterface(id, this.getRepository());

		if (interfaze.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the interface with the given ID.");
		} else {
			this.uncertaintySources.add(new InterfaceUncertaintySource(interfaze.get(), propagationHelper));
		}
	}

	public void addConnectorUncertainty(String id) {
		var connector = this.propagationHelper.findConnector(id, this.getSystem());

		if (connector.isEmpty()) {
			throw new IllegalArgumentException("Unable to find the connector with the given ID.");
		} else {
			this.uncertaintySources.add(ConnectorUncertaintySource.of(connector.get(), propagationHelper));
		}
	}

}

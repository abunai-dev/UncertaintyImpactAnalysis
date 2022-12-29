package edu.kit.kastel.dsis.uncertainty.impactanalysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.StandalonePCMDataFlowConfidentialtyAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.UncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ActorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.BehaviorUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.ComponentUncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

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
}
package edu.kit.kastel.dsis.uncertainty.impactanalysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.StandalonePCMDataFlowConfidentialtyAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.UncertaintyImpact;
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
	
	public void propagate() {
		for (UncertaintySource<?> uncertaintySource: uncertaintySources) {
			var element = uncertaintySource.getArchitecturalElement();
			System.out.println(element);
			
			var uncertaintyImpacts = uncertaintySource.propagate();
			System.out.println(uncertaintyImpacts);
			
			for (UncertaintyImpact<?> uncertaintyImpact : uncertaintyImpacts) {
				var affectedElement = uncertaintyImpact.getAffectedElement();
				System.out.println(affectedElement);
				
				var affectedDataFlow = uncertaintyImpact.getAffectedDataFlow();
				System.out.println(affectedDataFlow);
			}
		}
	}

	public void addComponentUncertainty(String id) {
		var component = this.propagationHelper.findAssemblyContext(id);

		if (component.isEmpty()) {
			throw new IllegalArgumentException("Unable to find an assembly context with the given ID.");
		} else {
			this.uncertaintySources.add(new ComponentUncertaintySource(component.get(), propagationHelper));
		}

	}
}

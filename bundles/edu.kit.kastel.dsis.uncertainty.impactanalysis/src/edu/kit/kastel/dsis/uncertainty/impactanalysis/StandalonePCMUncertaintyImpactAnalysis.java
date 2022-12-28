package edu.kit.kastel.dsis.uncertainty.impactanalysis;

import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.StandalonePCMDataFlowConfidentialtyAnalysis;

public class StandalonePCMUncertaintyImpactAnalysis extends StandalonePCMDataFlowConfidentialtyAnalysis {

	public StandalonePCMUncertaintyImpactAnalysis(String modelProjectName,
			Class<? extends Plugin> modelProjectActivator, String relativeUsageModelPath,
			String relativeAllocationModelPath) {
		super(modelProjectName, modelProjectActivator, relativeUsageModelPath, relativeAllocationModelPath);
	}

	public int countActionSequences() {
		return super.findAllSequences().size();
	}

	@Override
	public boolean initalizeAnalysis() {
		return super.initalizeAnalysis();
	}
}

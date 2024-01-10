package dev.abunai.impact.analysis;

import org.dataflowanalysis.analysis.pcm.PCMDataFlowConfidentialityAnalysisBuilder;

public class PCMUncertaintyImpactAnalysisBuilder extends PCMDataFlowConfidentialityAnalysisBuilder {
	
	@Override
	public PCMUncertaintyImpactAnalysis build() {
		return new PCMUncertaintyImpactAnalysis(this.createAnalysisData(), modelProjectName, pluginActivator);

	}
	
}

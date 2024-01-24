package dev.abunai.impact.analysis;

import org.dataflowanalysis.analysis.pcm.PCMDataFlowConfidentialityAnalysisBuilder;
import org.dataflowanalysis.analysis.pcm.core.PCMDataCharacteristicsCalculatorFactory;
import org.dataflowanalysis.analysis.pcm.core.PCMNodeCharacteristicsCalculator;
import org.dataflowanalysis.analysis.pcm.resource.PCMResourceProvider;
import org.dataflowanalysis.analysis.pcm.resource.PCMURIResourceProvider;
import org.dataflowanalysis.analysis.utils.ResourceUtils;

public class PCMUncertaintyImpactAnalysisBuilder extends PCMDataFlowConfidentialityAnalysisBuilder {

	@Override
	public PCMUncertaintyImpactAnalysis build() {
		// Hint: Does not allow for custom resource providers
		PCMResourceProvider resourceProvider = new PCMURIResourceProvider(
				ResourceUtils.createRelativePluginURI(relativeUsageModelPath, modelProjectName),
				ResourceUtils.createRelativePluginURI(relativeAllocationModelPath, modelProjectName),
				ResourceUtils.createRelativePluginURI(relativeNodeCharacteristicsPath, modelProjectName));
		return new PCMUncertaintyImpactAnalysis(new PCMNodeCharacteristicsCalculator(resourceProvider),
				new PCMDataCharacteristicsCalculatorFactory(resourceProvider), resourceProvider, modelProjectName,
				pluginActivator);

	}
	
	public PCMDataFlowConfidentialityAnalysisBuilder useCustomResourceProvider(PCMResourceProvider resourceProvider) {
		throw new IllegalArgumentException("Custom resource providers are not supported by the uncertainty impact analysis.");
	}
}

package dev.abunai.impact.analysis;

import org.dataflowanalysis.analysis.pcm.PCMDataFlowConfidentialityAnalysisBuilder;
import org.dataflowanalysis.analysis.pcm.resource.PCMResourceProvider;
import org.dataflowanalysis.analysis.pcm.resource.PCMURIResourceProvider;
import org.dataflowanalysis.analysis.utils.ResourceUtils;

/**
 * Analysis Builder object used to create a {@link PCMUncertaintyImpactAnalysis}
 * <b>Warning:</b> Cannot use custom resource providers
 */
public class PCMUncertaintyImpactAnalysisBuilder extends PCMDataFlowConfidentialityAnalysisBuilder {

	@Override
	public PCMUncertaintyImpactAnalysis build() {
		PCMResourceProvider resourceProvider = new PCMURIResourceProvider(
				ResourceUtils.createRelativePluginURI(relativeUsageModelPath, modelProjectName),
				ResourceUtils.createRelativePluginURI(relativeAllocationModelPath, modelProjectName),
				ResourceUtils.createRelativePluginURI(relativeNodeCharacteristicsPath, modelProjectName));
		return new PCMUncertaintyImpactAnalysis(resourceProvider, modelProjectName,
				pluginActivator);

	}

	/**
	 * Uses the custom provided resource provider for the analysis.
	 * <b>Warning:</b> Is currently not supported by the analysis
	 * @param resourceProvider Custom resource provider that is used by the analysis
	 * @return Returns the analysis builder object
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder useCustomResourceProvider(PCMResourceProvider resourceProvider) {
		throw new IllegalArgumentException("Custom resource providers are not supported by the uncertainty impact analysis.");
	}
}

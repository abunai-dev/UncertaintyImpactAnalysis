package dev.abunai.impact.analysis;

import org.dataflowanalysis.analysis.builder.AbstractDataFlowAnalysisBuilder;
import org.dataflowanalysis.analysis.builder.pcm.PCMAnalysisBuilderData;

public class PCMUncertaintyImpactAnalysisBuilder extends
		AbstractDataFlowAnalysisBuilder<StandalonePCMUncertaintyImpactAnalysis, PCMUncertaintyImpactAnalysisBuilderData, PCMAnalysisBuilderData> {

	public PCMUncertaintyImpactAnalysisBuilder() {
		super(new PCMUncertaintyImpactAnalysisBuilderData());
	}

	@Override
	public void copyBuilderData(PCMAnalysisBuilderData builderData) {
		super.builderData.setModelProjectName(builderData.getModelProjectName());
		super.builderData.setPluginActivator(builderData.getPluginActivator());
		super.builderData.setRelativeAllocationModelPath(builderData.getRelativeAllocationModelPath());
		super.builderData.setRelativeNodeCharacteristicsPath(builderData.getRelativeNodeCharacteristicsPath());
		super.builderData.setRelativeUsageModelPath(builderData.getRelativeUsageModelPath());
		super.builderData.setStandalone(builderData.isStandalone());
	}

	@Override
	public void validateBuilderData() {
		this.builder.forEach(it -> it.validateBuilderData());
		this.builderData.validateData();
	}

	@Override
	public StandalonePCMUncertaintyImpactAnalysis build() {
		this.validateBuilderData();

		return new StandalonePCMUncertaintyImpactAnalysis(builderData.createAnalysisData(),
				this.builderData.getModelProjectName(), builderData.getPluginActivator());
	}

}

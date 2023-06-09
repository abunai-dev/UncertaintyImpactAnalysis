package dev.abunai.impact.analysis;

import org.palladiosimulator.dataflow.confidentiality.analysis.builder.pcm.PCMAnalysisBuilderData;

public class PCMUncertaintyImpactAnalysisBuilderData extends PCMAnalysisBuilderData {

	@Override
	public void validateData() {
		if (this.isLegacy()) {
			throw new IllegalStateException("Legacy EMF profiles are not supported by the uncertainty analysis.");
		}
	}

}

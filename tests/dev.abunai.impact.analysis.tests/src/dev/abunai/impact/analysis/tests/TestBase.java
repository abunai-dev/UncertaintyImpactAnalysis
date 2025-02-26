package dev.abunai.impact.analysis.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.eclipse.core.runtime.Plugin;
import org.junit.jupiter.api.BeforeEach;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysisBuilder;
import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;
import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.testmodels.Activator;

public abstract class TestBase {
	protected PCMUncertaintyImpactAnalysis analysis;

	protected abstract String getFolderName();

	protected abstract String getFilesName();
	
	protected String getModelProjectName() {
		return "dev.abunai.impact.analysis.testmodels";
	}
	
	protected Class<? extends Plugin> getActivator() {
		return Activator.class;
	}

	protected String getBaseFolder() {
		return "models";
	}

	@BeforeEach
	public void setup() {
		final var usageModelPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".usagemodel")
				.toString();
		final var allocationPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".allocation")
				.toString();
		final var nodeCharacteristicsPath = Paths
				.get(getBaseFolder(), getFolderName(), getFilesName() + ".nodecharacteristics").toString();

		var builder = new PCMUncertaintyImpactAnalysisBuilder().standalone()
				.modelProjectName(this.getModelProjectName())
				.usePluginActivator(this.getActivator())
				.useUsageModel(usageModelPath)
				.useAllocationModel(allocationPath)
				.useNodeCharacteristicsModel(nodeCharacteristicsPath);

		var analysis = ((PCMUncertaintyImpactAnalysisBuilder) builder).build();
		analysis.initializeAnalysis();
		
		this.analysis = analysis;
	}

	protected void assertAnalysisResults(int resultSize, int numberOfDataFlowSections, int impactSetSize) {
		UncertaintyImpactCollection analysisResult = analysis.propagate();

		assertEquals(resultSize, analysisResult.getUncertaintyImpacts().size());
		assertEquals(numberOfDataFlowSections, analysisResult.getImpactSet(false).size());
		assertEquals(impactSetSize, analysisResult.getImpactSet(true).size());
	}

}

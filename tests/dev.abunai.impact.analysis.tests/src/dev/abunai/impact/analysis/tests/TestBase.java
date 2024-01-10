package dev.abunai.impact.analysis.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysisBuilder;
import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;
import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.testmodels.Activator;

public abstract class TestBase {

	public static final String TEST_MODEL_PROJECT_NAME = "dev.abunai.impact.analysis.testmodels";
	protected PCMUncertaintyImpactAnalysis analysis = null;

	protected abstract String getFolderName();

	protected abstract String getFilesName();

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

		var builder = new PCMUncertaintyImpactAnalysisBuilder().standalone().modelProjectName(TEST_MODEL_PROJECT_NAME)
				.usePluginActivator(Activator.class).useUsageModel(usageModelPath).useAllocationModel(allocationPath)
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

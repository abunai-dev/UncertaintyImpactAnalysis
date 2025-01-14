package dev.abunai.impact.analysis;

import dev.abunai.impact.analysis.interactive.InteractiveAnalysisHandler;
import dev.abunai.impact.analysis.testmodels.Activator;

import java.io.IOException;
import java.nio.file.Paths;

public class ImpactAnalysisCLI {
    private static final String MODEL_PATH = "models/BranchingOnlineShop/default";
    public static final String TEST_MODEL_PROJECT_NAME = "dev.abunai.impact.analysis.testmodels";

    public static void main(String[] args) throws IOException {
        final var usageModelPath = Paths.get(MODEL_PATH + ".usagemodel")
                .toString();
        final var allocationPath = Paths.get(MODEL_PATH + ".allocation")
                .toString();
        final var nodeCharacteristicsPath = Paths
                .get(MODEL_PATH + ".nodecharacteristics").toString();

        var builder = new PCMUncertaintyImpactAnalysisBuilder().standalone().modelProjectName(TEST_MODEL_PROJECT_NAME)
                .usePluginActivator(Activator.class).useUsageModel(usageModelPath).useAllocationModel(allocationPath)
                .useNodeCharacteristicsModel(nodeCharacteristicsPath);

        var analysis = ((PCMUncertaintyImpactAnalysisBuilder) builder).build();
        analysis.initializeAnalysis();
        new InteractiveAnalysisHandler(analysis).handle();
    }
}

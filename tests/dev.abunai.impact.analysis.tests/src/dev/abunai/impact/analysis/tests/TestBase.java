package dev.abunai.impact.analysis.tests;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;

import dev.abunai.impact.analysis.StandalonePCMUncertaintyImpactAnalysis;
import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.testmodels.Activator;

public abstract class TestBase {

	public static final String TEST_MODEL_PROJECT_NAME = "dev.abunai.impact.analysis.testmodels";
	protected StandalonePCMUncertaintyImpactAnalysis analysis = null;

	protected abstract String getFolderName();

	protected abstract String getFilesName();
	
	protected String getBaseFolder() {
		return "models";
	}

	@BeforeEach
	public void setup() {
		final var usageModelPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".usagemodel").toString();
		final var allocationPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".allocation").toString();
		final var repositoryPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".repository").toString();
		final var systemPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".system").toString();

		var analysis = new StandalonePCMUncertaintyImpactAnalysis(TEST_MODEL_PROJECT_NAME, Activator.class,
				usageModelPath, allocationPath, repositoryPath, systemPath);
		analysis.initalizeAnalysis();

		this.analysis = analysis;
	}

	protected void printResultsWithTitle(List<UncertaintyImpact<?>> result, String title,
			boolean newLineAfterEachEntry) {
		System.out.println("Results of: " + title);
		this.printResults(result, false, false, true, newLineAfterEachEntry);
	}

	protected void printResults(List<UncertaintyImpact<?>> result, boolean newLineAfterEachEntry) {
		this.printResults(result, false, false, true, newLineAfterEachEntry);
	}

	protected void printResults(List<UncertaintyImpact<?>> result, boolean printDetails, boolean printOverview,
			boolean printFinalImpactSet, boolean newLineAfterEachEntry) {

		if (printDetails) {
			result.forEach(System.out::println);
		}

		List<AbstractPCMActionSequenceElement<?>> allAffectedElements = analysis
				.getAllAffectedElementsAfterPropagation();
		Set<ActionSequence> impactSet = analysis.getImpactSet(false);
		Set<ActionSequence> distinctImpactSet = analysis.getImpactSet(true);

		if (printOverview) {
			System.out.printf("\n\nAll affected elements (%d):\n", allAffectedElements.size());
			allAffectedElements.forEach(System.out::println);

			System.out.printf("\n\nImpacted data flow sections (%d):\n", impactSet.size());
			impactSet.stream().map(
					it -> formatDataFlow(analysis.getActionSequenceIndex(it.getElements()), it, newLineAfterEachEntry))
					.forEach(System.out::println);
		}

		if (printFinalImpactSet) {
			System.out.printf("\n\nDistinct Impact set (%d):\n", distinctImpactSet.size());
			distinctImpactSet.stream().map(
					it -> formatDataFlow(analysis.getActionSequenceIndex(it.getElements()), it, newLineAfterEachEntry))
					.forEach(System.out::println);
		}

		System.out.println("\n\n");
	}

	protected String formatDataFlow(int index, ActionSequence sequence, boolean newLineAfterEachEntry) {
		return String.format("%d: %s", index, sequence.getElements().stream().map(it -> it.toString())
				.collect(Collectors.joining(newLineAfterEachEntry ? "\n" : ", ")));
	}

}

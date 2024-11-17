package dev.abunai.impact.analysis.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dataflowanalysis.analysis.core.AbstractVertex;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.CallReturnBehavior;
import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;

public class UncertaintyImpactCollection {

	private final List<UncertaintyImpact<?>> uncertaintyImpacts;
	private final PCMFlowGraphCollection actionSequences;

	public UncertaintyImpactCollection(PCMFlowGraphCollection actionSequences,
			List<UncertaintyImpact<?>> uncertaintyImpacts) {
		this.actionSequences = actionSequences;
		this.uncertaintyImpacts = uncertaintyImpacts;
	}

	public List<UncertaintyImpact<?>> getUncertaintyImpacts() {
		return this.uncertaintyImpacts;
	}

	public List<AbstractVertex<?>> getAllAffectedElementsAfterPropagation() {
		return uncertaintyImpacts.stream().map(it -> it.getAffectedElement()).collect(Collectors.toList());
	}

	public List<PCMTransposeFlowGraph> getAllAffectedDataFlowSectionsAfterPropagation() {
		return uncertaintyImpacts.stream().map(it -> it.getAffectedDataFlowSections()).flatMap(Collection::stream)
				.toList();
	}

	public Set<PCMTransposeFlowGraph> getImpactSet(boolean distinct) {
		List<PCMTransposeFlowGraph> allAffectedSequences = this.getAllAffectedDataFlowSectionsAfterPropagation();

		Set<PCMTransposeFlowGraph> impactSet = new HashSet<>();
		for (PCMTransposeFlowGraph actionSequence : allAffectedSequences) {
			// TODO: There is a bug here in the original code
			// TODO: The code find semi-distinct ones (e.g. filters away returns, calls and starting elements remained
			if (impactSet.stream().anyMatch(it -> {
				var otherNodes = it.getVertices().stream()
						.map(vertex -> (AbstractPCMVertex<?>) vertex)
						.toList();
				var ownNodes = actionSequence.getVertices().stream()
						.map(AbstractPCMVertex.class::cast)
						.toList();

				var otherPCMElements = otherNodes.stream()
						.map(AbstractPCMVertex::getReferencedElement)
						.toList();
				var ownPCMElements = ownNodes.stream()
						.map(AbstractPCMVertex::getReferencedElement)
						.toList();

				return otherPCMElements.equals(ownPCMElements); //otherPCMElements.containsAll(ownPCMElements) && ownPCMElements.containsAll(otherPCMElements); //&& actionSequence.getVertices().size() < it.getVertices().size();
			})) {
				continue;
			} else {
				impactSet.add(actionSequence);
			}
		}

		if (distinct) {
			Set<PCMTransposeFlowGraph> entriesToRemove = new HashSet<>();
			for (PCMTransposeFlowGraph actionSequence : impactSet) {
				List<PCMTransposeFlowGraph> similarDataFlows = impactSet.stream().filter(it -> getActionSequenceIndex(
						it.getVertices()) == getActionSequenceIndex(actionSequence.getVertices())).toList();

				for (PCMTransposeFlowGraph similarDataFlow : similarDataFlows) {
					if (similarDataFlow.equals(actionSequence)) {
						continue;
					} else if (actionSequence.getVertices().size() >= similarDataFlow.getVertices().size()) {
						entriesToRemove.add(similarDataFlow);
					} else {
						entriesToRemove.add(actionSequence);
					}
				}
			}
			impactSet.removeAll(entriesToRemove);
		}

		return impactSet;
	}

	// TODO: This is broken
	public int getActionSequenceIndex(List<? extends AbstractVertex<?>> entries) {
		for (int i = 0; i < this.actionSequences.getTransposeFlowGraphs().size(); i++) {
			var elements = this.actionSequences.getTransposeFlowGraphs().get(i).getVertices().stream()
					.map(it -> (AbstractPCMVertex<?>) it).toList();

			boolean matches = entries.stream()
					.allMatch(vertex -> elements.stream()
							.map(it -> (AbstractPCMVertex<?>) it)
							.anyMatch(it -> it.isEquivalentInContext(vertex)));

			if (matches) {
				return i;
			}
		}

		return -1;
	}

	public void printResultsWithTitle(String title, boolean newLineAfterEachEntry) {
		System.out.println("Results of: " + title);
		this.printResults(false, false, true, newLineAfterEachEntry);
	}

	public void printResults(boolean newLineAfterEachEntry) {
		this.printResults(false, false, true, newLineAfterEachEntry);
	}

	public void printResults(boolean printDetails, boolean printOverview, boolean printFinalImpactSet,
			boolean newLineAfterEachEntry) {

		if (printDetails) {
			this.getUncertaintyImpacts().forEach(System.out::println);
		}

		List<AbstractVertex<?>> allAffectedElements = this.getAllAffectedElementsAfterPropagation();
		Set<PCMTransposeFlowGraph> impactSet = this.getImpactSet(false);
		Set<PCMTransposeFlowGraph> distinctImpactSet = this.getImpactSet(true);

		if (printOverview) {
			System.out.printf("\n\nAll affected elements (%d):\n", allAffectedElements.size());
			allAffectedElements.forEach(System.out::println);

			System.out.printf("\n\nImpacted data flow sections (%d):\n", impactSet.size());
			impactSet.stream()
					.map(it -> formatDataFlow(this.getActionSequenceIndex(it.getVertices()), it, newLineAfterEachEntry))
					.forEach(System.out::println);
		}

		if (printFinalImpactSet) {
			System.out.printf("\n\nDistinct Impact set (%d):\n", distinctImpactSet.size());
			distinctImpactSet.stream()
					.map(it -> formatDataFlow(this.getActionSequenceIndex(it.getVertices()), it, newLineAfterEachEntry))
					.forEach(System.out::println);
		}

		System.out.println("\n\n");
	}

	public static String formatDataFlow(int index, PCMTransposeFlowGraph sequence, boolean newLineAfterEachEntry) {
		try {
			return String.format("%d: %s", index, sequence.getVertices().stream().map(AbstractVertex::toString)
					.collect(Collectors.joining(newLineAfterEachEntry ? "\n" : ", ")));
		} catch (NullPointerException e) {
			return "[Exception while formatting]";
		}
	}

	public static String formatDataFlow(int index, List<? extends AbstractVertex<?>> elements, boolean newLineAfterEachEntry) {
		try {
			return String.format("%d: %s", index, elements.stream().map(AbstractVertex::toString)
					.collect(Collectors.joining(newLineAfterEachEntry ? "\n" : ", ")));
		} catch (NullPointerException e) {
			return "[Exception while formatting]";
		}
	}
}

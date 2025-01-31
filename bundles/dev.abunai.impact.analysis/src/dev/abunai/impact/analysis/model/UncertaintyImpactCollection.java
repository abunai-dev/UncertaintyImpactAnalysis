package dev.abunai.impact.analysis.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.dataflowanalysis.analysis.core.AbstractVertex;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;

public class UncertaintyImpactCollection {
	private static final Logger logger = Logger.getLogger(UncertaintyImpactCollection.class);
	private final List<UncertaintyImpact<?>> uncertaintyImpacts;
	private final PCMFlowGraphCollection flowGraphs;

	public UncertaintyImpactCollection(PCMFlowGraphCollection flowGraphs,
			List<UncertaintyImpact<?>> uncertaintyImpacts) {
		this.flowGraphs = flowGraphs;
		this.uncertaintyImpacts = uncertaintyImpacts;
	}

	public List<UncertaintyImpact<?>> getUncertaintyImpacts() {
		return this.uncertaintyImpacts;
	}

	public List<AbstractVertex<?>> getAllAffectedElementsAfterPropagation() {
		return uncertaintyImpacts.stream()
				.map(UncertaintyImpact::getAffectedElement)
				.collect(Collectors.toList());
	}

	public List<PCMTransposeFlowGraph> getAllAffectedDataFlowSectionsAfterPropagation() {
		return uncertaintyImpacts.stream().map(UncertaintyImpact::getAffectedDataFlowSections)
				.flatMap(Collection::stream)
				.toList();
	}

	public Set<PCMTransposeFlowGraph> getImpactSet(boolean distinct) {
		List<PCMTransposeFlowGraph> affectedTransposeFlowGraphs = this.getAllAffectedDataFlowSectionsAfterPropagation();

		Set<PCMTransposeFlowGraph> impactSet = new HashSet<>();
		for (PCMTransposeFlowGraph transposeFlowGraph : affectedTransposeFlowGraphs) {
			if (impactSet.stream().anyMatch(it -> {
				var otherNodes = it.getVertices().stream()
						.map(vertex -> (AbstractPCMVertex<?>) vertex)
						.toList();
				var ownNodes = transposeFlowGraph.getVertices().stream()
						.map(AbstractPCMVertex.class::cast)
						.toList();

				var otherPCMElements = otherNodes.stream()
						.map(AbstractPCMVertex::getReferencedElement)
						.toList();
				var ownPCMElements = ownNodes.stream()
						.map(AbstractPCMVertex::getReferencedElement)
						.toList();

				return otherPCMElements.equals(ownPCMElements);
			})) {
				continue;
			} else {
				impactSet.add(transposeFlowGraph);
			}
		}

		if (distinct) {
			Set<PCMTransposeFlowGraph> entriesToRemove = new HashSet<>();
			for (PCMTransposeFlowGraph transposeFlowGraph : impactSet) {
				List<PCMTransposeFlowGraph> similarDataFlows = impactSet.stream().filter(it -> getFlowGraphIndex(
						it.getVertices()) == getFlowGraphIndex(transposeFlowGraph.getVertices())).toList();

				for (PCMTransposeFlowGraph similarDataFlow : similarDataFlows) {
					if (similarDataFlow.equals(transposeFlowGraph)) {
						continue;
					} else if (transposeFlowGraph.getVertices().size() >= similarDataFlow.getVertices().size()) {
						entriesToRemove.add(similarDataFlow);
					} else {
						entriesToRemove.add(transposeFlowGraph);
					}
				}
			}
			impactSet.removeAll(entriesToRemove);
		}

		return impactSet;
	}

	public int getFlowGraphIndex(List<? extends AbstractVertex<?>> entries) {
		for (int i = 0; i < this.flowGraphs.getTransposeFlowGraphs().size(); i++) {
			var elements = this.flowGraphs.getTransposeFlowGraphs().get(i).getVertices().stream()
					.map(it -> (AbstractPCMVertex<?>) it).toList();

			boolean matches = true;
			for (var entry : entries) {
				if (elements.stream().noneMatch(it -> it.isEquivalentInContext(entry))) {
					matches = false;
					break;
				}
			}

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
					.map(it -> formatDataFlow(this.getFlowGraphIndex(it.getVertices()), it, newLineAfterEachEntry))
					.forEach(System.out::println);
		}

		if (printFinalImpactSet) {
			System.out.printf("\n\nDistinct Impact set (%d):\n", distinctImpactSet.size());
			distinctImpactSet.stream()
					.map(it -> formatDataFlow(this.getFlowGraphIndex(it.getVertices()), it, newLineAfterEachEntry))
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

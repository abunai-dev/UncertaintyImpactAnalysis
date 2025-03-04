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

/**
 * Contains a {@link PCMFlowGraphCollection} containing {@link PCMTransposeFlowGraph}
 * and a list of {@link UncertaintyImpact} that affect the contained transpose flow graphs
 */
public class UncertaintyImpactCollection {
	private static final Logger logger = Logger.getLogger(UncertaintyImpactCollection.class);

	private final List<UncertaintyImpact<?>> uncertaintyImpacts;
	private final PCMFlowGraphCollection flowGraphs;

	/**
	 * Creates a new {@link UncertaintyImpactCollection} with the given {@link PCMFlowGraphCollection} and uncertainty impacts
	 * @param flowGraphs Collection of flow graphs that are affected by the uncertainty impacts
	 * @param uncertaintyImpacts List of {@link UncertaintyImpact} that affect the flow graphs
	 */
	public UncertaintyImpactCollection(PCMFlowGraphCollection flowGraphs,
			List<UncertaintyImpact<?>> uncertaintyImpacts) {
		this.flowGraphs = flowGraphs;
		this.uncertaintyImpacts = uncertaintyImpacts;
		logger.trace("Created a uncertainty impact collection with " + uncertaintyImpacts.size() + " uncertainty impacts!");
	}

	/**
	 * Returns the list of {@link UncertaintyImpact} that affect the stored transpose flow graphs
	 * @return Returns the list of affecting {@link UncertaintyImpact}
	 */
	public List<UncertaintyImpact<?>> getUncertaintyImpacts() {
		return this.uncertaintyImpacts;
	}

	/**
	 * Returns a collection of all affected elements after the {@link UncertaintyImpact} have been propagated
	 * @return Returns a list of affected vertices by the contained collection of {@link UncertaintyImpact}
	 */
	public List<AbstractVertex<?>> getAllAffectedElementsAfterPropagation() {
		return uncertaintyImpacts.stream()
				.map(UncertaintyImpact::getAffectedElement)
				.collect(Collectors.toList());
	}

	/**
	 * Returns a collection of all affected data flow sections after the {@link UncertaintyImpact} have been propagated
	 * @return Returns a list of data flow sections affected by the contained collection of {@link UncertaintyImpact}
	 */
	public List<PCMTransposeFlowGraph> getAllAffectedDataFlowSectionsAfterPropagation() {
		return uncertaintyImpacts.stream().map(UncertaintyImpact::getAffectedDataFlowSections)
				.flatMap(Collection::stream)
				.toList();
	}

	/**
	 * Returns the impact set containing all impacted transpose flow graph sections
	 * @param distinct If set to true, only return unique affected transpose flow graph sections
	 * @return Returns a set of transpose flow graph sections that are affected by the contained {@link UncertaintyImpact}
	 */
	public Set<PCMTransposeFlowGraph> getImpactSet(boolean distinct) {
		List<PCMTransposeFlowGraph> affectedTransposeFlowGraphs = this.getAllAffectedDataFlowSectionsAfterPropagation();

		Set<PCMTransposeFlowGraph> impactSet = new HashSet<>();
		for (PCMTransposeFlowGraph transposeFlowGraph : affectedTransposeFlowGraphs) {
			if (impactSet.stream().noneMatch(it -> {
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
			}))  {
				impactSet.add(transposeFlowGraph);
			}
		}

		if (distinct) {
			Set<PCMTransposeFlowGraph> entriesToRemove = new HashSet<>();
			for (PCMTransposeFlowGraph transposeFlowGraph : impactSet) {
				List<PCMTransposeFlowGraph> similarDataFlows = impactSet.stream().filter(it -> getFlowGraphIndex(
						it.getVertices()) == getFlowGraphIndex(transposeFlowGraph.getVertices())).toList();

				for (PCMTransposeFlowGraph similarDataFlow : similarDataFlows) {
                    if (!similarDataFlow.equals(transposeFlowGraph)) {
                        if (transposeFlowGraph.getVertices().size() >= similarDataFlow.getVertices().size()) {
                            entriesToRemove.add(similarDataFlow);
                        } else {
                            entriesToRemove.add(transposeFlowGraph);
                        }
                    }
                }
			}
			impactSet.removeAll(entriesToRemove);
		}

		return impactSet;
	}

	/**
	 * Determines the index into the list of transpose flow graphs of the given list of entries
	 * @param entries List of entries that must be contained in the transpose flow graph
	 * @return Return the index of the transpose flow graph that has the given entries.
	 * If none are found, the method returns -1
	 */
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
	/**
	 * Prints the impact set of the {@link UncertaintyImpactCollection} with optionally a new line after each entry.
	 * Additionally, includes a given title
	 * @param newLineAfterEachEntry Determines whether a newline should be printed after each affected element
	 */
	public void printResultsWithTitle(String title, boolean newLineAfterEachEntry) {
		System.out.println("Results of: " + title);
		this.printResults(newLineAfterEachEntry);
	}

	/**
	 * Prints the impact set of the {@link UncertaintyImpactCollection} with optionally a new line after each entry
	 * @param newLineAfterEachEntry Determines whether a newline should be printed after each affected element
	 */
	public void printResults(boolean newLineAfterEachEntry) {
		this.printResults(false, false, true, newLineAfterEachEntry);
	}

	/**
	 * Prints the results of the {@link UncertaintyImpact} propagation on the {@link org.dataflowanalysis.analysis.core.FlowGraphCollection}
	 * @param printDetails Determines whether uncertainty impacts should be verbosely printed
	 * @param printOverview Determines whether a overview over all elements should be printed
	 * @param printFinalImpactSet Determines whether the distinct impact set should be printed
	 * @param newLineAfterEachEntry Determines whether the overview and impact set should seperate elements with a new line
	 */
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

	/**
	 * Formats the given transpose flow graph with the given index and optionally seperates elements with a newline
	 * @param index Index of the transpose flow graph
	 * @param transposeFlowGraph Transpose flow graph that should be printed
	 * @param newLineAfterEachEntry If true, a newline will be printed after each vertex contained in the transpose flow graph
	 * @return Returns a formated string containing the transpose flow graph
	 */
	public static String formatDataFlow(int index, PCMTransposeFlowGraph transposeFlowGraph, boolean newLineAfterEachEntry) {
		try {
			return String.format("%d: %s", index, transposeFlowGraph.getVertices().stream()
					.map(AbstractVertex::toString)
					.collect(Collectors.joining(newLineAfterEachEntry ? "\n" : ", ")));
		} catch (NullPointerException e) {
			return "[Exception while formatting]";
		}
	}

	/**
	 * Formats the given list of vertices with the given index and optionally separates elements with a newline
	 * @param index Index of the vertices into the transpose flow graph collection
	 * @param elements Vertices that should be printed
	 * @param newLineAfterEachEntry If true, a newline will be printed after each vertex contained in the transpose flow graph
	 * @return Returns a formated string containing the vertices
	 */
	public static String formatDataFlow(int index, List<? extends AbstractVertex<?>> elements, boolean newLineAfterEachEntry) {
		try {
			return String.format("%d: %s", index, elements.stream().map(AbstractVertex::toString)
					.collect(Collectors.joining(newLineAfterEachEntry ? "\n" : ", ")));
		} catch (NullPointerException e) {
			return "[Exception while formatting]";
		}
	}
}

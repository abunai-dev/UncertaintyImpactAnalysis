package dev.abunai.impact.analysis.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dataflowanalysis.analysis.core.AbstractActionSequenceElement;
import org.dataflowanalysis.analysis.core.ActionSequence;
import org.dataflowanalysis.analysis.core.pcm.AbstractPCMActionSequenceElement;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;

public class UncertaintyImpactCollection {

	private final List<UncertaintyImpact<?>> uncertaintyImpacts;
	private final List<ActionSequence> actionSequences;

	public UncertaintyImpactCollection(List<ActionSequence> actionSequences,
			List<UncertaintyImpact<?>> uncertaintyImpacts) {
		this.actionSequences = actionSequences;
		this.uncertaintyImpacts = uncertaintyImpacts;
	}

	public List<UncertaintyImpact<?>> getUncertaintyImpacts() {
		return this.uncertaintyImpacts;
	}

	public List<AbstractActionSequenceElement<?>> getAllAffectedElementsAfterPropagation() {
		return uncertaintyImpacts.stream().map(it -> it.getAffectedElement()).collect(Collectors.toList());
	}

	public List<ActionSequence> getAllAffectedDataFlowSectionsAfterPropagation() {
		return uncertaintyImpacts.stream().map(it -> it.getAffectedDataFlowSections()).flatMap(Collection::stream)
				.toList();
	}

	public Set<ActionSequence> getImpactSet(boolean distinct) {
		List<ActionSequence> allAffectedSequences = this.getAllAffectedDataFlowSectionsAfterPropagation();

		Set<ActionSequence> impactSet = new HashSet<ActionSequence>();
		for (ActionSequence actionSequence : allAffectedSequences) {
			if (impactSet.stream().anyMatch(it -> {
				var otherNodes = it.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast).toList();
				var ownNodes = actionSequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
						.toList();

				var otherPCMElements = otherNodes.stream().map(AbstractPCMActionSequenceElement.class::cast)
						.map(AbstractPCMActionSequenceElement::getElement).toList();
				var ownPCMElements = ownNodes.stream().map(AbstractPCMActionSequenceElement.class::cast)
						.map(AbstractPCMActionSequenceElement::getElement).toList();

				return otherPCMElements.equals(ownPCMElements);
			})) {
				continue;
			} else {
				impactSet.add(actionSequence);
			}
		}

		if (distinct) {
			Set<ActionSequence> entriesToRemove = new HashSet<ActionSequence>();
			for (ActionSequence actionSequence : impactSet) {
				List<ActionSequence> similarDataFlows = impactSet.stream().filter(it -> getActionSequenceIndex(
						it.getElements()) == getActionSequenceIndex(actionSequence.getElements())).toList();

				for (ActionSequence similarDataFlow : similarDataFlows) {
					if (similarDataFlow.equals(actionSequence)) {
						continue;
					} else if (actionSequence.getElements().size() >= similarDataFlow.getElements().size()) {
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

	public int getActionSequenceIndex(List<AbstractActionSequenceElement<?>> entries) {
		for (int i = 0; i < this.actionSequences.size(); i++) {
			var elements = this.actionSequences.get(i).getElements().stream()
					.map(AbstractActionSequenceElement.class::cast).toList();

			if (Collections.indexOfSubList(elements, entries) != -1) {
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

		List<AbstractActionSequenceElement<?>> allAffectedElements = this.getAllAffectedElementsAfterPropagation();
		Set<ActionSequence> impactSet = this.getImpactSet(false);
		Set<ActionSequence> distinctImpactSet = this.getImpactSet(true);

		if (printOverview) {
			System.out.printf("\n\nAll affected elements (%d):\n", allAffectedElements.size());
			allAffectedElements.forEach(System.out::println);

			System.out.printf("\n\nImpacted data flow sections (%d):\n", impactSet.size());
			impactSet.stream()
					.map(it -> formatDataFlow(this.getActionSequenceIndex(it.getElements()), it, newLineAfterEachEntry))
					.forEach(System.out::println);
		}

		if (printFinalImpactSet) {
			System.out.printf("\n\nDistinct Impact set (%d):\n", distinctImpactSet.size());
			distinctImpactSet.stream()
					.map(it -> formatDataFlow(this.getActionSequenceIndex(it.getElements()), it, newLineAfterEachEntry))
					.forEach(System.out::println);
		}

		System.out.println("\n\n");
	}

	public static String formatDataFlow(int index, ActionSequence sequence, boolean newLineAfterEachEntry) {
		try {
			return String.format("%d: %s", index, sequence.getElements().stream().map(it -> it.toString())
					.collect(Collectors.joining(newLineAfterEachEntry ? "\n" : ", ")));
		} catch (NullPointerException e) {
			return "[Exception while formatting]";
		}
	}

}

package edu.kit.kastel.dsis.uncertainty.impactanalysis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.StartAction;

public class PropagationHelper {

	private List<ActionSequence> actionSequences;

	public PropagationHelper(List<ActionSequence> actionSequences) {
		this.actionSequences = actionSequences;
	}

	public Optional<AssemblyContext> findAssemblyContext(String id) {
		for (ActionSequence sequence : actionSequences) {
			List<?> allContexts = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
					.map(it -> it.getContext()).flatMap(Collection::stream).toList();

			Optional<AssemblyContext> match = allContexts.stream().filter(AssemblyContext.class::isInstance)
					.map(AssemblyContext.class::cast).filter(it -> EcoreUtil.getID(it).equals(id)).findFirst();

			return match;
		}
		return Optional.empty();
	}

	public Optional<? extends Entity> findAction(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SEFFActionSequenceElement<StartAction>> findStartActionsOfAssemblyContext(AssemblyContext component) {
		List<SEFFActionSequenceElement<StartAction>> matches = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			@SuppressWarnings("unchecked")
			var candidates = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
					.filter(it -> it instanceof SEFFActionSequenceElement)
					.filter(it -> (it.getElement() instanceof StartAction))
					.filter(it -> it.getContext().contains(component))
					.map(it -> (SEFFActionSequenceElement<StartAction>) it).toList();

			matches.addAll(candidates);
		}

		return matches;
	}

	public List<? extends AbstractPCMActionSequenceElement<?>> findProccessesWithActionOfType(Entity action) {
		// TODO Auto-generated method stub
		return null;
	}

	public Optional<ActionSequence> findActionSequenceWithElement(AbstractActionSequenceElement<?> element) {
		return actionSequences.stream().filter(it -> it.getElements().contains(element)).findFirst();
	}

}

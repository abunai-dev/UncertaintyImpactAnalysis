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
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.repository.OperationalDataStoreComponent;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

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
		for (ActionSequence sequence : actionSequences) {
			var candidates = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
					.filter(it -> EcoreUtil.getID(it.getElement()).equals(id)).toList();

			return candidates.stream().map(AbstractPCMActionSequenceElement::getElement)
					.filter(Entity.class::isInstance).map(Entity.class::cast).findFirst();
		}

		return Optional.empty();
	}

	public Optional<EnumCharacteristic> findEnumCharacteristicAnnotation(String id) {
		for (ActionSequence sequence : actionSequences) {
			var elements = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast).toList();

			for (AbstractPCMActionSequenceElement<?> node : elements) {
				var architectureElement = node.getElement();

				if (architectureElement instanceof AbstractUserAction || architectureElement instanceof AbstractAction
						|| architectureElement instanceof OperationalDataStoreComponent) {

					var calculator = new TracingPCMNodeCharacteristicsCalculator((Entity) architectureElement, id);
					if (calculator.isAnnotatedWithNodeCharacteristic(node.getContext())) {
						// TODO: Find correct object and get correct element. Optional.empty() is wrong
						// and only a placeholder!
						// TODO: This tracing might require to completely rewrite the
						// CharacteristicsCalculator. If this is not the case, make a PR to the DFD
						// repository.
						return Optional.empty();
					}
				}
			}
		}

		return Optional.empty();
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

	public List<AbstractPCMActionSequenceElement<?>> findProccessesWithAction(Entity action) {
		List<AbstractPCMActionSequenceElement<?>> matches = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			var candidates = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
					.filter(it -> it.getElement().equals(action)).map(it -> (AbstractPCMActionSequenceElement<?>) it)
					.toList();

			matches.addAll(candidates);
		}

		return matches;
	}

	public Optional<ActionSequence> findActionSequenceWithElement(AbstractActionSequenceElement<?> element) {
		return actionSequences.stream().filter(it -> it.getElements().contains(element)).findFirst();
	}

}

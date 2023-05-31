package dev.abunai.impact.analysis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.resource.ResourceLoader;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.CallingSEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.user.CallingUserActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.seff.SEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.repository.OperationalDataStoreComponent;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

public class PropagationHelper {

	private List<ActionSequence> actionSequences;
	private final ResourceLoader resourceLoader;

	public PropagationHelper(List<ActionSequence> actionSequences, ResourceLoader resourceLoader) {
		this.actionSequences = actionSequences;
		this.resourceLoader = resourceLoader;
	}

	public Optional<AssemblyContext> findAssemblyContext(String id) {
		List<Deque<AssemblyContext>> contexts = this.findAllAssemblyContexts();
		List<AssemblyContext> allContexts = contexts.stream().flatMap(Collection::stream).toList();
		return allContexts.stream().filter(it -> EcoreUtil.getID(it).equals(id)).findFirst();
	}

	public Optional<? extends Entity> findAction(String id) {
		for (ActionSequence sequence : actionSequences) {
			var candidates = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
					.filter(it -> EcoreUtil.getID(it.getElement()).equals(id)).toList();

			if(candidates.size() > 0) {
				return candidates.stream().map(AbstractPCMActionSequenceElement::getElement)
						.filter(Entity.class::isInstance).map(Entity.class::cast).findFirst();
			}
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

					var calculator = new TracingPCMNodeCharacteristicsCalculator((Entity) architectureElement, id, resourceLoader);
					if (calculator.isAnnotatedWithNodeCharacteristic(node.getContext())) {
						return calculator.getNodeCharacteristics(node.getContext()).stream().findFirst();
					}
				}
			}
		}

		return Optional.empty();
	}

	public Optional<OperationInterface> findInterface(String id, Repository repository) {
		return repository.getInterfaces__Repository().stream().filter(it -> it.getId().equals(id))
				.filter(OperationInterface.class::isInstance).map(OperationInterface.class::cast).findFirst();
	}

	public Optional<Connector> findConnector(String id, System system) {
		return system.getConnectors__ComposedStructure().stream().filter(it -> it.getId().equals(id)).findFirst();
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

	public List<AbstractPCMActionSequenceElement<?>> findProcessesWithAnnotation(EnumCharacteristic annotation) {
		List<AbstractPCMActionSequenceElement<?>> matches = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			for (AbstractActionSequenceElement<?> node : sequence.getElements()) {
				var pcmNode = (AbstractPCMActionSequenceElement<?>) node;

				if (pcmNode.getElement() instanceof Entity) {
					var calculator = new TracingPCMNodeCharacteristicsCalculator((Entity) pcmNode.getElement(),
							annotation.getId(), resourceLoader);

					if (calculator.isAnnotatedWithNodeCharacteristic(pcmNode.getContext())) {
						matches.add(pcmNode);
					}
				}
			}
		}

		return matches;
	}

	public List<CallingUserActionSequenceElement> findEntryLevelSystemCallsViaInterface(OperationInterface interfaze) {
		List<CallingUserActionSequenceElement> matches = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			var entryLevelSystemCalls = sequence.getElements().stream()
					.filter(CallingUserActionSequenceElement.class::isInstance)
					.map(CallingUserActionSequenceElement.class::cast).toList();
			var entryLevelSystemCallsCandidates = entryLevelSystemCalls.stream()
					.filter(it -> interfaze.getSignatures__OperationInterface()
							.contains(it.getElement().getOperationSignature__EntryLevelSystemCall()))
					.toList();
			matches.addAll(entryLevelSystemCallsCandidates);

		}

		return matches;
	}

	public List<CallingSEFFActionSequenceElement> findExternalCallsViaInterface(OperationInterface interfaze) {
		List<CallingSEFFActionSequenceElement> matches = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			var externalCalls = sequence.getElements().stream()
					.filter(CallingSEFFActionSequenceElement.class::isInstance)
					.map(CallingSEFFActionSequenceElement.class::cast).toList();

			var externalCallCandidates = externalCalls.stream().filter(it -> interfaze
					.getSignatures__OperationInterface().contains(it.getElement().getCalledService_ExternalService()))
					.toList();
			matches.addAll(externalCallCandidates);
		}

		return matches;
	}

	public List<SEFFActionSequenceElement<StartAction>> findStartActionsOfSEFFsThatImplement(
			OperationInterface interfaze) {
		List<SEFFActionSequenceElement<StartAction>> matches = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			@SuppressWarnings("unchecked")
			var startActions = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
					.filter(it -> it instanceof SEFFActionSequenceElement)
					.filter(it -> (it.getElement() instanceof StartAction))
					.map(it -> (SEFFActionSequenceElement<StartAction>) it).toList();

			for (SEFFActionSequenceElement<StartAction> action : startActions) {
				if (action.getElement().eContainer() instanceof ResourceDemandingSEFF seff) {
					if (interfaze.getSignatures__OperationInterface().contains(seff.getDescribedService__SEFF())) {
						matches.add(action);
					}
				}
			}
		}

		return matches;
	}

	public List<ActionSequence> findActionSequencesWithElement(AbstractActionSequenceElement<?> element) {
		return actionSequences.stream().filter(it -> it.getElements().contains(element)).toList();
	}

	private List<Deque<AssemblyContext>> findAllAssemblyContexts() {
		List<Deque<AssemblyContext>> allContexts = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			List<Deque<AssemblyContext>> contexts = sequence.getElements().stream()
					.map(AbstractPCMActionSequenceElement.class::cast).map(it -> it.getContext())
					.collect(Collectors.toList());

			allContexts.addAll(contexts);
		}

		return allContexts;
	}

}

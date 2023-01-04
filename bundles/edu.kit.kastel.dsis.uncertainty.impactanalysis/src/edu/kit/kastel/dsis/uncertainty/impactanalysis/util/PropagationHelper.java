package edu.kit.kastel.dsis.uncertainty.impactanalysis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.CallingSEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.CallingUserActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.pcm.PCMQueryUtils;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.pcm.SEFFWithContext;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.repository.OperationalDataStoreComponent;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

public class PropagationHelper {

	private List<ActionSequence> actionSequences;

	public PropagationHelper(List<ActionSequence> actionSequences) {
		this.actionSequences = actionSequences;
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
							annotation.getId());

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

//	public List<BasicComponent> findComponentsThatImplement(OperationInterface interfaze) {
//		Repository repository = interfaze.getRepository__Interface();
//		List<BasicComponent> components = repository.getComponents__Repository().stream()
//				.filter(BasicComponent.class::isInstance).map(BasicComponent.class::cast).toList();
//		
//		for(BasicComponent component : components) {
//			List<Signature> signatures = component.getServiceEffectSpecifications__BasicComponent().stream().map(it -> it.getDescribedService__SEFF()).toList();
//		}
//		
//		return null;
//	}

	public List<StartAction> findStartActionsOfSEFFsThatImplement(OperationInterface interfaze) {
		List<Optional<SEFFWithContext>> candidates = new ArrayList<>();

		List<OperationSignature> signatures = interfaze.getSignatures__OperationInterface();
		List<Deque<AssemblyContext>> allContexts = this.findAllAssemblyContexts().stream().filter(it -> !it.isEmpty())
				.toList();

		for (Deque<AssemblyContext> contexts : allContexts) {
			RepositoryComponent component = contexts.getLast().getEncapsulatedComponent__AssemblyContext();

			for (OperationSignature signature : signatures) {
				for (RequiredRole role : component.getRequiredRoles_InterfaceRequiringEntity()) {
					candidates.add(PCMQueryUtils.findCalledSEFF(role, signature, contexts));
				}
			}
		}

		List<SEFFWithContext> filteredCandidates = candidates.stream().flatMap(Optional::stream).toList();
		return filteredCandidates.stream().map(it -> it.seff().getSteps_Behaviour().get(0))
				.filter(StartAction.class::isInstance).map(StartAction.class::cast).distinct().toList();
	}

	public Optional<ActionSequence> findActionSequenceWithElement(AbstractActionSequenceElement<?> element) {
		return actionSequences.stream().filter(it -> it.getElements().contains(element)).findFirst();
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

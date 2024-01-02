package dev.abunai.impact.analysis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dataflowanalysis.analysis.core.ActionSequence;
import org.dataflowanalysis.analysis.resource.ResourceProvider;
import org.dataflowanalysis.analysis.utils.pcm.PCMQueryUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentPackage;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemPackage;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.dataflowanalysis.analysis.core.AbstractActionSequenceElement;
import org.dataflowanalysis.analysis.core.pcm.AbstractPCMActionSequenceElement;
import org.dataflowanalysis.analysis.core.pcm.seff.CallingSEFFActionSequenceElement;
import org.dataflowanalysis.analysis.core.pcm.seff.SEFFActionSequenceElement;
import org.dataflowanalysis.analysis.core.pcm.user.CallingUserActionSequenceElement;

public class PropagationHelper {

	private List<ActionSequence> actionSequences;
	private final ResourceProvider resourceProvider;

	public PropagationHelper(List<ActionSequence> actionSequences, ResourceProvider resourceProvider) {
		this.actionSequences = actionSequences;
		this.resourceProvider = resourceProvider;
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

			if (candidates.size() > 0) {
				return candidates.stream().map(AbstractPCMActionSequenceElement::getElement)
						.filter(Entity.class::isInstance).map(Entity.class::cast).findFirst();
			}
		}

		return Optional.empty();
	}

	public Optional<OperationInterface> findInterface(String id) {
		return lookupRepositoryModel().getInterfaces__Repository().stream().filter(it -> it.getId().equals(id))
				.filter(OperationInterface.class::isInstance).map(OperationInterface.class::cast).findFirst();
	}

	public Optional<OperationSignature> findSignature(String id) {
		return lookupRepositoryModel().getInterfaces__Repository().stream().filter(OperationInterface.class::isInstance)
				.map(OperationInterface.class::cast).map(it -> it.getSignatures__OperationInterface())
				.flatMap(Collection::stream).filter(it -> it.getId().equals(id)).findFirst();

	}

	public Optional<Connector> findConnector(String id) {
		return lookupSystemModel().getConnectors__ComposedStructure().stream().filter(it -> it.getId().equals(id))
				.findFirst();
	}

	public Optional<ResourceContainer> findResourceContainer(String id) {
		return lookupResourceEnvironmentModel().getResourceContainer_ResourceEnvironment().stream()
				.filter(it -> it.getId().equals(id)).findFirst();
	}

	public Optional<UsageScenario> findUsageScenario(String id) {
		return lookupUsageModel().getUsageScenario_UsageModel().stream().filter(it -> it.getId().equals(id))
				.findFirst();
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

	public List<CallingUserActionSequenceElement> findEntryLevelSystemCallsViaSignature(OperationSignature signature) {
		var candidates = findEntryLevelSystemCallsViaInterface(signature.getInterface__OperationSignature());

		return candidates.stream()
				.filter(it -> it.getElement().getOperationSignature__EntryLevelSystemCall().equals(signature)).toList();
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

	public List<CallingSEFFActionSequenceElement> findExternalCallsViaSignature(OperationSignature signature) {
		var candidates = this.findExternalCallsViaInterface(signature.getInterface__OperationSignature());

		return candidates.stream().filter(it -> it.getElement().getCalledService_ExternalService().equals(signature))
				.toList();
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

	public List<SEFFActionSequenceElement<StartAction>> findStartActionsOfSEFFsThatImplement(
			OperationSignature signature) {
		var actionsThatImplementInterface = this
				.findStartActionsOfSEFFsThatImplement(signature.getInterface__OperationSignature());

		List<SEFFActionSequenceElement<StartAction>> matches = new ArrayList<>();

		for (SEFFActionSequenceElement<StartAction> action : actionsThatImplementInterface) {
			if (action.getElement().eContainer() instanceof ResourceDemandingSEFF seff) {
				if (signature.equals(seff.getDescribedService__SEFF())) {
					matches.add(action);
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

	public List<? extends AbstractPCMActionSequenceElement<?>> findProcessesThatRepresentResourceContainerOrUsageScenario(
			Entity actor) {

		if (actor instanceof UsageScenario usageScenario) {
			List<CallingUserActionSequenceElement> matches = new ArrayList<>();

			for (ActionSequence sequence : actionSequences) {
				var callingUserActions = sequence.getElements().stream()
						.filter(CallingUserActionSequenceElement.class::isInstance)
						.map(CallingUserActionSequenceElement.class::cast).toList();

				List<CallingUserActionSequenceElement> candidates = callingUserActions.stream()
						.filter(it -> it.getElement().getScenarioBehaviour_AbstractUserAction()
								.getUsageScenario_SenarioBehaviour().equals(usageScenario))
						.toList();

				matches.addAll(candidates);
			}

			return matches;

		} else if (actor instanceof ResourceContainer resourceContainer) {

			var allocationModel = lookupAllocationModel();

			var contextsDeployedOnResource = allocationModel.getAllocationContexts_Allocation().stream()
					.filter(it -> it.getResourceContainer_AllocationContext().equals(resourceContainer))
					.map(it -> it.getAssemblyContext_AllocationContext()).toList();

			List<SEFFActionSequenceElement<?>> matches = new ArrayList<>();

			for (ActionSequence sequence : actionSequences) {
				var candidates = sequence.getElements().stream().filter(SEFFActionSequenceElement.class::isInstance)
						.map(it -> (SEFFActionSequenceElement<?>) it)
						.filter(it -> it.getContext().stream().anyMatch(contextsDeployedOnResource::contains)).toList();

				matches.addAll(candidates);
			}

			return matches;

		} else {
			throw new IllegalArgumentException("Actor must be an usage scenario or a resource container.");
		}
	}

	private <T extends NamedElement> T lookupPCMModel(EClass eclazz, Class<T> clazz) {
		Objects.requireNonNull(eclazz);
		Objects.requireNonNull(clazz);

		List<T> allPCMModelsOfGivenType = resourceProvider.lookupElementOfType(eclazz).stream().filter(clazz::isInstance)
				.map(clazz::cast).toList();

		if (allPCMModelsOfGivenType.size() == 1) {
			return allPCMModelsOfGivenType.get(0);
		} else {
			throw new IllegalStateException(String.format(
					"None or more than one model of type %s found in the loaded resources.", clazz.getSimpleName()));
		}
	}

	private Repository lookupRepositoryModel() {
		return lookupPCMModel(RepositoryPackage.eINSTANCE.getRepository(), Repository.class);
	}

	private System lookupSystemModel() {
		return lookupPCMModel(SystemPackage.eINSTANCE.getSystem(), System.class);
	}

	private ResourceEnvironment lookupResourceEnvironmentModel() {
		return lookupPCMModel(ResourceenvironmentPackage.eINSTANCE.getResourceEnvironment(), ResourceEnvironment.class);
	}

	private Allocation lookupAllocationModel() {
		return resourceProvider.getAllocation();
	}

	private UsageModel lookupUsageModel() {
		return resourceProvider.getUsageModel();
	}

	public List<StartAction> findStartActionsOfBranchAction(String id) {
		List<StartAction> matches = new ArrayList<>();

		for (ActionSequence sequence : actionSequences) {
			var startActionElements = sequence.getElements().stream().map(AbstractPCMActionSequenceElement.class::cast)
					.filter(it -> it instanceof SEFFActionSequenceElement)
					.filter(it -> it.getElement() instanceof StartAction).toList();

			for (var sequenceElement : startActionElements) {
				Optional<BranchAction> branchAction = PCMQueryUtils.findParentOfType(sequenceElement.getElement(),
						BranchAction.class, false);

				if (branchAction.isPresent() && branchAction.get().getId().equals(id)) {
					matches.add((StartAction) sequenceElement.getElement());
				}
			}
		}

		return matches;
	}

}

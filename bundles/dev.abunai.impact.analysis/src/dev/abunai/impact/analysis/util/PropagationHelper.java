package dev.abunai.impact.analysis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dataflowanalysis.analysis.core.AbstractTransposeFlowGraph;
import org.dataflowanalysis.analysis.core.FlowGraphCollection;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.dataflowanalysis.analysis.pcm.core.seff.CallingSEFFPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.seff.SEFFPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.user.CallingUserPCMVertex;
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
import org.dataflowanalysis.analysis.pcm.resource.PCMResourceProvider;
import org.dataflowanalysis.analysis.pcm.utils.PCMQueryUtils;

public class PropagationHelper {

	private PCMFlowGraphCollection actionSequences;
	private final PCMResourceProvider resourceProvider;

	public PropagationHelper(PCMFlowGraphCollection actionSequences, PCMResourceProvider resourceProvider) {
		this.actionSequences = actionSequences;
		this.resourceProvider = resourceProvider;
	}

	public Optional<AssemblyContext> findAssemblyContext(String id) {
		List<Deque<AssemblyContext>> contexts = this.findAllAssemblyContexts();
		List<AssemblyContext> allContexts = contexts.stream().flatMap(Collection::stream).toList();
		return allContexts.stream().filter(it -> EcoreUtil.getID(it).equals(id)).findFirst();
	}

	public Optional<? extends Entity> findAction(String id) {
		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			var candidates = sequence.getVertices().stream().map(AbstractPCMVertex.class::cast)
					.filter(it -> EcoreUtil.getID(it.getReferencedElement()).equals(id)).toList();

			if (candidates.size() > 0) {
				return candidates.stream().map(AbstractPCMVertex::getReferencedElement)
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

	public List<SEFFPCMVertex<StartAction>> findStartActionsOfAssemblyContext(AssemblyContext component) {
		List<SEFFPCMVertex<StartAction>> matches = new ArrayList<>();

		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			@SuppressWarnings("unchecked")
			var candidates = sequence.getVertices().stream().map(AbstractPCMVertex.class::cast)
					.filter(it -> it instanceof SEFFPCMVertex<?>)
					.filter(it -> (it.getReferencedElement() instanceof StartAction))
					.filter(it -> it.getContext().contains(component))
					.map(it -> (SEFFPCMVertex<StartAction>) it).toList();

			matches.addAll(candidates);
		}

		return matches;
	}

	public List<AbstractPCMVertex<?>> findProccessesWithAction(Entity action) {
		List<AbstractPCMVertex<?>> matches = new ArrayList<>();

		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			var candidates = sequence.getVertices().stream().map(AbstractPCMVertex.class::cast)
					.filter(it -> it.getReferencedElement().equals(action)).map(it -> (AbstractPCMVertex<?>) it)
					.toList();

			matches.addAll(candidates);
		}

		return matches;
	}

	public List<CallingUserPCMVertex> findEntryLevelSystemCallsViaInterface(OperationInterface interfaze) {
		List<CallingUserPCMVertex> matches = new ArrayList<>();

		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			var entryLevelSystemCalls = sequence.getVertices().stream()
					.filter(CallingUserPCMVertex.class::isInstance)
					.map(CallingUserPCMVertex.class::cast).toList();
			var entryLevelSystemCallsCandidates = entryLevelSystemCalls.stream()
					.filter(it -> interfaze.getSignatures__OperationInterface()
							.contains(it.getReferencedElement().getOperationSignature__EntryLevelSystemCall()))
					.toList();
			matches.addAll(entryLevelSystemCallsCandidates);

		}

		return matches;
	}

	public List<CallingUserPCMVertex> findEntryLevelSystemCallsViaSignature(OperationSignature signature) {
		var candidates = findEntryLevelSystemCallsViaInterface(signature.getInterface__OperationSignature());

		return candidates.stream()
				.filter(it -> it.getReferencedElement().getOperationSignature__EntryLevelSystemCall().equals(signature)).toList();
	}

	public List<CallingSEFFPCMVertex> findExternalCallsViaInterface(OperationInterface interfaze) {
		List<CallingSEFFPCMVertex> matches = new ArrayList<>();

		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			var externalCalls = sequence.getVertices().stream()
					.filter(CallingSEFFPCMVertex.class::isInstance)
					.map(CallingSEFFPCMVertex.class::cast).toList();

			var externalCallCandidates = externalCalls.stream().filter(it -> interfaze
					.getSignatures__OperationInterface().contains(it.getReferencedElement().getCalledService_ExternalService()))
					.toList();
			matches.addAll(externalCallCandidates);
		}

		return matches;
	}

	public List<CallingSEFFPCMVertex> findExternalCallsViaSignature(OperationSignature signature) {
		var candidates = this.findExternalCallsViaInterface(signature.getInterface__OperationSignature());

		return candidates.stream().filter(it -> it.getReferencedElement().getCalledService_ExternalService().equals(signature))
				.toList();
	}

	public List<SEFFPCMVertex<StartAction>> findStartActionsOfSEFFsThatImplement(
			OperationInterface interfaze) {
		List<SEFFPCMVertex<StartAction>> matches = new ArrayList<>();

		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			@SuppressWarnings("unchecked")
			var startActions = sequence.getVertices().stream().map(AbstractPCMVertex.class::cast)
					.filter(it -> it instanceof SEFFPCMVertex<?>)
					.filter(it -> (it.getReferencedElement() instanceof StartAction))
					.map(it -> (SEFFPCMVertex<StartAction>) it).toList();

			for (SEFFPCMVertex<StartAction> action : startActions) {
				if (action.getReferencedElement().eContainer() instanceof ResourceDemandingSEFF seff) {
					if (interfaze.getSignatures__OperationInterface().contains(seff.getDescribedService__SEFF())) {
						matches.add(action);
					}
				}
			}
		}

		return matches;
	}

	public List<SEFFPCMVertex<StartAction>> findStartActionsOfSEFFsThatImplement(
			OperationSignature signature) {
		var actionsThatImplementInterface = this
				.findStartActionsOfSEFFsThatImplement(signature.getInterface__OperationSignature());

		List<SEFFPCMVertex<StartAction>> matches = new ArrayList<>();

		for (SEFFPCMVertex<StartAction> action : actionsThatImplementInterface) {
			if (action.getReferencedElement().eContainer() instanceof ResourceDemandingSEFF seff) {
				if (signature.equals(seff.getDescribedService__SEFF())) {
					matches.add(action);
				}
			}
		}

		return matches;
	}

	public List<PCMTransposeFlowGraph> findActionSequencesWithElement(AbstractPCMVertex<?> element) {
		return actionSequences.getTransposeFlowGraphs().stream()
				.filter(PCMTransposeFlowGraph.class::isInstance)
				.filter(it -> it.getVertices().contains(element))
				.map(PCMTransposeFlowGraph.class::cast)
				.toList();
	}

	private List<Deque<AssemblyContext>> findAllAssemblyContexts() {
		List<Deque<AssemblyContext>> allContexts = new ArrayList<>();

		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			for (AbstractPCMVertex<?> vertex : sequence.getVertices().stream()
					.filter(AbstractPCMVertex.class::isInstance)
					.map(AbstractPCMVertex.class::cast)
					.toList()) {
				allContexts.add(vertex.getContext());
			}
		}

		return allContexts;
	}

	public List<? extends AbstractPCMVertex<?>> findProcessesThatRepresentResourceContainerOrUsageScenario(
			Entity actor) {

		if (actor instanceof UsageScenario usageScenario) {
			List<CallingUserPCMVertex> matches = new ArrayList<>();

			for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
				var callingUserActions = sequence.getVertices().stream()
						.filter(CallingUserPCMVertex.class::isInstance)
						.map(CallingUserPCMVertex.class::cast).toList();

				List<CallingUserPCMVertex> candidates = callingUserActions.stream()
						.filter(it -> it.getReferencedElement().getScenarioBehaviour_AbstractUserAction()
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

			List<SEFFPCMVertex<?>> matches = new ArrayList<>();

			for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
				var candidates = sequence.getVertices().stream().filter(SEFFPCMVertex.class::isInstance)
						.map(it -> (SEFFPCMVertex<?>) it)
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

		List<T> allPCMModelsOfGivenType = resourceProvider.lookupToplevelElement(eclazz).stream().filter(clazz::isInstance)
				.map(clazz::cast).toList();

		// TODO: Simple Data Types are also contained in a repository model. Changing this should be fine
		if (!allPCMModelsOfGivenType.isEmpty()) {
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

		for (AbstractTransposeFlowGraph sequence : actionSequences.getTransposeFlowGraphs()) {
			var startActionElements = sequence.getVertices().stream().map(AbstractPCMVertex.class::cast)
					.filter(it -> it instanceof SEFFPCMVertex<?>)
					.filter(it -> it.getReferencedElement() instanceof StartAction).toList();

			for (var sequenceElement : startActionElements) {
				Optional<BranchAction> branchAction = PCMQueryUtils.findParentOfType(sequenceElement.getReferencedElement(),
						BranchAction.class, false);

				if (branchAction.isPresent() && branchAction.get().getId().equals(id)) {
					matches.add((StartAction) sequenceElement.getReferencedElement());
				}
			}
		}

		return matches;
	}

}

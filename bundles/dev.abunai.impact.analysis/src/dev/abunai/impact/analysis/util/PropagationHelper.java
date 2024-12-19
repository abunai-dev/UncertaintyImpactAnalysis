package dev.abunai.impact.analysis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.dataflowanalysis.analysis.core.AbstractTransposeFlowGraph;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph;
import org.dataflowanalysis.analysis.pcm.core.seff.CallingSEFFPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.seff.SEFFPCMVertex;
import org.dataflowanalysis.analysis.pcm.core.user.CallingUserPCMVertex;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
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

	private final PCMFlowGraphCollection flowGraphs;
	private final PCMResourceProvider resourceProvider;

	public PropagationHelper(PCMFlowGraphCollection flowGraphs, PCMResourceProvider resourceProvider) {
		this.flowGraphs = flowGraphs;
		this.resourceProvider = resourceProvider;
	}

	public Optional<AssemblyContext> findAssemblyContext(String id) {
		List<Deque<AssemblyContext>> contexts = this.findAllAssemblyContexts();
		List<AssemblyContext> allContexts = contexts.stream().flatMap(Collection::stream).toList();
		return allContexts.stream().filter(it -> EcoreUtil.getID(it).equals(id)).findFirst();
	}

	public Optional<? extends Entity> findAction(String id) {
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var candidates = transposeFlowGraph.getVertices().stream()
					.map(AbstractPCMVertex.class::cast)
					.filter(it -> EcoreUtil.getID(it.getReferencedElement()).equals(id))
					.toList();

			if (!candidates.isEmpty()) {
				return candidates.stream().map(AbstractPCMVertex::getReferencedElement)
						.filter(Objects::nonNull).findFirst();
			}
		}

		return Optional.empty();
	}

	public Optional<OperationInterface> findInterface(String id) {
		return this.lookupRepositoryModel().getInterfaces__Repository().stream()
				.filter(it -> it.getId().equals(id))
				.filter(OperationInterface.class::isInstance)
				.map(OperationInterface.class::cast)
				.findFirst();
	}

	public Optional<OperationSignature> findSignature(String id) {
		return this.lookupRepositoryModel().getInterfaces__Repository().stream()
				.filter(OperationInterface.class::isInstance)
				.map(OperationInterface.class::cast)
				.map(OperationInterface::getSignatures__OperationInterface)
				.flatMap(Collection::stream)
				.filter(it -> it.getId().equals(id))
				.findFirst();

	}

	public Optional<Connector> findConnector(String id) {
		return this.lookupSystemModel().getConnectors__ComposedStructure().stream()
				.filter(it -> it.getId().equals(id))
				.findFirst();
	}

	public Optional<ResourceContainer> findResourceContainer(String id) {
		return this.lookupResourceEnvironmentModel().getResourceContainer_ResourceEnvironment().stream()
				.filter(it -> it.getId().equals(id))
				.findFirst();
	}

	public Optional<UsageScenario> findUsageScenario(String id) {
		return this.lookupUsageModel().getUsageScenario_UsageModel().stream()
				.filter(it -> it.getId().equals(id))
				.findFirst();
	}

	public List<SEFFPCMVertex<?>> findStartActionsOfAssemblyContext(AssemblyContext component) {
		List<SEFFPCMVertex<?>> matches = new ArrayList<>();

		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var candidates = transposeFlowGraph.getVertices().stream().map(AbstractPCMVertex.class::cast)
					.filter(it -> it instanceof SEFFPCMVertex<?>)
					.filter(it -> it.getReferencedElement() instanceof StartAction)
					.filter(it -> it.getContext().contains(component))
					.map(it -> (SEFFPCMVertex<?>) it)
					.toList();
			matches.addAll(candidates);
		}
		return matches;
	}

	public List<AbstractPCMVertex<?>> findProcessesWithAction(Entity action) {
		List<AbstractPCMVertex<?>> matches = new ArrayList<>();
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var candidates = transposeFlowGraph.getVertices().stream()
					.map(AbstractPCMVertex.class::cast)
					.filter(it -> it.getReferencedElement().equals(action))
					.map(it -> (AbstractPCMVertex<?>) it)
					.toList();

			matches.addAll(candidates);
		}
		return matches;
	}

	public List<CallingUserPCMVertex> findEntryLevelSystemCallsViaInterface(OperationInterface interfaze) {
		List<CallingUserPCMVertex> matches = new ArrayList<>();
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var entryLevelSystemCalls = transposeFlowGraph.getVertices().stream()
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
				.filter(it -> it.getReferencedElement().getOperationSignature__EntryLevelSystemCall().equals(signature))
				.toList();
	}

	public List<CallingSEFFPCMVertex> findExternalCallsViaInterface(OperationInterface interfaze) {
		List<CallingSEFFPCMVertex> matches = new ArrayList<>();
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var externalCalls = transposeFlowGraph.getVertices().stream()
					.filter(CallingSEFFPCMVertex.class::isInstance)
					.map(CallingSEFFPCMVertex.class::cast).toList();
			var externalCallCandidates = externalCalls.stream()
					.filter(it -> interfaze.getSignatures__OperationInterface().contains(it.getReferencedElement().getCalledService_ExternalService()))
					.toList();
			matches.addAll(externalCallCandidates);
		}
		return matches;
	}

	public List<CallingSEFFPCMVertex> findExternalCallsViaSignature(OperationSignature signature) {
		var candidates = this.findExternalCallsViaInterface(signature.getInterface__OperationSignature());
		return candidates.stream()
				.filter(it -> it.getReferencedElement().getCalledService_ExternalService().equals(signature))
				.toList();
	}

	public List<SEFFPCMVertex<?>> findStartActionsOfSEFFsThatImplement(
			OperationInterface interfaze) {
		List<SEFFPCMVertex<?>> matches = new ArrayList<>();
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var startActions = transposeFlowGraph.getVertices().stream()
					.map(AbstractPCMVertex.class::cast)
					.filter(it -> it instanceof SEFFPCMVertex<?>)
					.filter(it -> (it.getReferencedElement() instanceof StartAction))
					.map(it -> (SEFFPCMVertex<?>) it)
					.toList();

			for (SEFFPCMVertex<?> action : startActions) {
				if (!(action.getReferencedElement().eContainer() instanceof ResourceDemandingSEFF seff)) {
					continue;
				}
				if (!(seff.getDescribedService__SEFF() instanceof OperationSignature operationSignature)) {
					continue;
				}
				if (interfaze.getSignatures__OperationInterface().contains(operationSignature)) {
					matches.add(action);
				}
			}
		}
		return matches;
	}

	public List<SEFFPCMVertex<?>> findStartActionsOfSEFFsThatImplement(
			OperationSignature signature) {
		var actionsThatImplementInterface = this
				.findStartActionsOfSEFFsThatImplement(signature.getInterface__OperationSignature());
		List<SEFFPCMVertex<?>> matches = new ArrayList<>();
		for (SEFFPCMVertex<?> action : actionsThatImplementInterface) {
			if (action.getReferencedElement().eContainer() instanceof ResourceDemandingSEFF seff) {
				if (signature.equals(seff.getDescribedService__SEFF())) {
					matches.add(action);
				}
			}
		}
		return matches;
	}

	public List<PCMTransposeFlowGraph> findActionSequencesWithElement(AbstractPCMVertex<?> element) {
		return flowGraphs.getTransposeFlowGraphs().stream()
				.filter(PCMTransposeFlowGraph.class::isInstance)
				.filter(it -> it.getVertices().contains(element))
				.map(PCMTransposeFlowGraph.class::cast)
				.toList();
	}

	private List<Deque<AssemblyContext>> findAllAssemblyContexts() {
		List<Deque<AssemblyContext>> allContexts = new ArrayList<>();
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			for (AbstractPCMVertex<?> vertex : transposeFlowGraph.getVertices().stream()
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
			for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
				var callingUserActions = transposeFlowGraph.getVertices().stream()
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
			var allocationModel = this.lookupAllocationModel();
			var contextsDeployedOnResource = allocationModel.getAllocationContexts_Allocation().stream()
					.filter(it -> it.getResourceContainer_AllocationContext().equals(resourceContainer))
					.map(AllocationContext::getAssemblyContext_AllocationContext).toList();
			List<SEFFPCMVertex<?>> matches = new ArrayList<>();
			for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
				var candidates = transposeFlowGraph.getVertices().stream().filter(SEFFPCMVertex.class::isInstance)
						.map(it -> (SEFFPCMVertex<?>) it)
						.filter(it -> it.getContext().stream().anyMatch(contextsDeployedOnResource::contains))
						.toList();
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
		List<T> allPCMModelsOfGivenType = resourceProvider.lookupToplevelElement(eclazz).stream()
				.filter(clazz::isInstance)
				.map(clazz::cast).toList();
		if (!allPCMModelsOfGivenType.isEmpty()) {
			return allPCMModelsOfGivenType.get(0);
		} else {
			throw new IllegalStateException(String.format(
					"None or more than one model of type %s found in the loaded resources.", clazz.getSimpleName()));
		}
	}

	private Repository lookupRepositoryModel() {
		return this.lookupPCMModel(RepositoryPackage.eINSTANCE.getRepository(), Repository.class);
	}

	private System lookupSystemModel() {
		return this.lookupPCMModel(SystemPackage.eINSTANCE.getSystem(), System.class);
	}

	private ResourceEnvironment lookupResourceEnvironmentModel() {
		return this.lookupPCMModel(ResourceenvironmentPackage.eINSTANCE.getResourceEnvironment(), ResourceEnvironment.class);
	}

	private Allocation lookupAllocationModel() {
		return this.resourceProvider.getAllocation();
	}

	private UsageModel lookupUsageModel() {
		return this.resourceProvider.getUsageModel();
	}

	public List<StartAction> findStartActionsOfBranchAction(String id) {
		List<StartAction> matches = new ArrayList<>();

		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var startActionElements = transposeFlowGraph.getVertices().stream().map(AbstractPCMVertex.class::cast)
					.filter(it -> it instanceof SEFFPCMVertex<?>)
					.filter(it -> it.getReferencedElement() instanceof StartAction).toList();

			for (var vertex : startActionElements) {
				Optional<BranchAction> branchAction = PCMQueryUtils.findParentOfType(vertex.getReferencedElement(),
						BranchAction.class, false);

				if (branchAction.isPresent() && branchAction.get().getId().equals(id)) {
					matches.add((StartAction) vertex.getReferencedElement());
				}
			}
		}

		return matches;
	}

}

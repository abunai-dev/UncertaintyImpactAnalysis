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

/**
 * A utility class used for a {@link PCMFlowGraphCollection} to determine and find correct corresponding elements
 */
public class PropagationHelper {
	private final PCMFlowGraphCollection flowGraphs;
	private final PCMResourceProvider resourceProvider;

	/**
	 * Creates a new {@link PropagationHelper} with the given {@link PCMFlowGraphCollection}
	 * @param flowGraphs {@link PCMFlowGraphCollection} that contains the elements searched in the analysis
	 * @param resourceProvider Resource provider that supplements the data contained in the flow graph collection
	 */
	public PropagationHelper(PCMFlowGraphCollection flowGraphs, PCMResourceProvider resourceProvider) {
		this.flowGraphs = flowGraphs;
		this.resourceProvider = resourceProvider;
	}

	/**
	 * Finds an {@link AssemblyContext} with the given ID
	 * @param id Given ID the {@link AssemblyContext} must have
	 * @return Returns an Optional containing the {@link AssemblyContext} if one can be found
	 */
	public Optional<AssemblyContext> findAssemblyContext(String id) {
		List<Deque<AssemblyContext>> contexts = this.findAllAssemblyContexts();
		List<AssemblyContext> allContexts = contexts.stream().flatMap(Collection::stream).toList();
		return allContexts.stream().filter(it -> EcoreUtil.getID(it).equals(id)).findFirst();
	}

	/**
	 * Finds an {@link Entity} with the given ID
	 * @param id Given ID the {@link Entity} must have
	 * @return Returns an Optional containing the {@link Entity} if one can be found
	 */
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

	/**
	 * Finds an {@link OperationInterface} with the given ID
	 * @param id Given ID the {@link OperationInterface} must have
	 * @return Returns an Optional containing the {@link OperationInterface} if one can be found
	 */
	public Optional<OperationInterface> findInterface(String id) {
		return this.lookupRepositoryModel().getInterfaces__Repository().stream()
				.filter(it -> it.getId().equals(id))
				.filter(OperationInterface.class::isInstance)
				.map(OperationInterface.class::cast)
				.findFirst();
	}

	/**
	 * Finds an {@link OperationSignature} with the given ID
	 * @param id Given ID the {@link OperationSignature} must have
	 * @return Returns an Optional containing the {@link OperationSignature} if one can be found
	 */
	public Optional<OperationSignature> findSignature(String id) {
		return this.lookupRepositoryModel().getInterfaces__Repository().stream()
				.filter(OperationInterface.class::isInstance)
				.map(OperationInterface.class::cast)
				.map(OperationInterface::getSignatures__OperationInterface)
				.flatMap(Collection::stream)
				.filter(it -> it.getId().equals(id))
				.findFirst();

	}

	/**
	 * Finds an {@link Connector} with the given ID
	 * @param id Given ID the {@link Connector} must have
	 * @return Returns an Optional containing the {@link Connector} if one can be found
	 */
	public Optional<Connector> findConnector(String id) {
		return this.lookupSystemModel().getConnectors__ComposedStructure().stream()
				.filter(it -> it.getId().equals(id))
				.findFirst();
	}

	/**
	 * Finds an {@link ResourceContainer} with the given ID
	 * @param id Given ID the {@link ResourceContainer} must have
	 * @return Returns an Optional containing the {@link ResourceContainer} if one can be found
	 */
	public Optional<ResourceContainer> findResourceContainer(String id) {
		return this.lookupResourceEnvironmentModel().getResourceContainer_ResourceEnvironment().stream()
				.filter(it -> it.getId().equals(id))
				.findFirst();
	}

	/**
	 * Finds an {@link UsageScenario} with the given ID
	 * @param id Given ID the {@link UsageScenario} must have
	 * @return Returns an Optional containing the {@link UsageScenario} if one can be found
	 */
	public Optional<UsageScenario> findUsageScenario(String id) {
		return this.lookupUsageModel().getUsageScenario_UsageModel().stream()
				.filter(it -> it.getId().equals(id))
				.findFirst();
	}

	/**
	 * Finds all {@link SEFFPCMVertex} that are start actions in the given {@link AssemblyContext}
	 * @param component {@link AssemblyContext} that must be started by the {@link SEFFPCMVertex} elements
	 * @return Returns a list of all {@link SEFFPCMVertex} that begin the {@link AssemblyContext}
	 */
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

	/**
	 * Determines all vertices that have the given {@link Entity} as referenced element
	 * @param action Given {@link Entity} that must be the referenced element of the vertex
	 * @return Returns a list of vertices with the given referenced {@link Entity}
	 */
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

	/**
	 * Finds all {@link CallingUserPCMVertex} that call using the given {@link OperationInterface}
	 * @param operationInterface {@link OperationInterface} that the {@link CallingUserPCMVertex} must call
	 * @return Returns a list of {@link CallingUserPCMVertex} that call using the given {@link OperationInterface}
	 */
	public List<CallingUserPCMVertex> findEntryLevelSystemCallsViaInterface(OperationInterface operationInterface) {
		List<CallingUserPCMVertex> matches = new ArrayList<>();
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var entryLevelSystemCalls = transposeFlowGraph.getVertices().stream()
					.filter(CallingUserPCMVertex.class::isInstance)
					.map(CallingUserPCMVertex.class::cast).toList();
			var entryLevelSystemCallsCandidates = entryLevelSystemCalls.stream()
					.filter(it -> operationInterface.getSignatures__OperationInterface()
							.contains(it.getReferencedElement().getOperationSignature__EntryLevelSystemCall()))
					.toList();
			matches.addAll(entryLevelSystemCallsCandidates);
		}
		return matches;
	}

	/**
	 * Finds all {@link CallingUserPCMVertex} that call using the given {@link OperationSignature}
	 * @param signature {@link OperationSignature} that the {@link CallingUserPCMVertex} must call
	 * @return Returns a list of {@link CallingUserPCMVertex} that call using the given {@link OperationSignature}
	 */
	public List<CallingUserPCMVertex> findEntryLevelSystemCallsViaSignature(OperationSignature signature) {
		var candidates = findEntryLevelSystemCallsViaInterface(signature.getInterface__OperationSignature());
		return candidates.stream()
				.filter(it -> it.getReferencedElement().getOperationSignature__EntryLevelSystemCall().equals(signature))
				.toList();
	}

	/**
	 * Finds all {@link CallingSEFFPCMVertex} that call using the given {@link OperationInterface}
	 * @param operationInterface {@link OperationInterface} that the {@link CallingSEFFPCMVertex} must call
	 * @return Returns a list of {@link CallingSEFFPCMVertex} that call using the given {@link OperationInterface}
	 */
	public List<CallingSEFFPCMVertex> findExternalCallsViaInterface(OperationInterface operationInterface) {
		List<CallingSEFFPCMVertex> matches = new ArrayList<>();
		for (AbstractTransposeFlowGraph transposeFlowGraph : flowGraphs.getTransposeFlowGraphs()) {
			var externalCalls = transposeFlowGraph.getVertices().stream()
					.filter(CallingSEFFPCMVertex.class::isInstance)
					.map(CallingSEFFPCMVertex.class::cast).toList();
			var externalCallCandidates = externalCalls.stream()
					.filter(it -> operationInterface.getSignatures__OperationInterface().contains(it.getReferencedElement().getCalledService_ExternalService()))
					.toList();
			matches.addAll(externalCallCandidates);
		}
		return matches;
	}

	/**
	 * Finds all {@link CallingSEFFPCMVertex} that call using the given {@link OperationSignature}
	 * @param signature {@link OperationSignature} that the {@link CallingSEFFPCMVertex} must call
	 * @return Returns a list of {@link CallingSEFFPCMVertex} that call using the given {@link OperationSignature}
	 */
	public List<CallingSEFFPCMVertex> findExternalCallsViaSignature(OperationSignature signature) {
		var candidates = this.findExternalCallsViaInterface(signature.getInterface__OperationSignature());
		return candidates.stream()
				.filter(it -> it.getReferencedElement().getCalledService_ExternalService().equals(signature))
				.toList();
	}

	/**
	 * Finds all the {@link SEFFPCMVertex} elements that are start actions of SEFFs that implement the given {@link OperationInterface}
	 * @param operationInterface {@link OperationInterface} the {@link SEFFPCMVertex} elements must start
	 * @return Returns all {@link SEFFPCMVertex} that are {@link StartAction} elements which start the implementation of the given {@link OperationInterface}
	 */
	public List<SEFFPCMVertex<?>> findStartActionsOfSEFFsThatImplement(
			OperationInterface operationInterface) {
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
				if (operationInterface.getSignatures__OperationInterface().contains(operationSignature)) {
					matches.add(action);
				}
			}
		}
		return matches;
	}

	/**
	 * Finds all the {@link SEFFPCMVertex} elements that are start actions of SEFFs that implement the given {@link OperationSignature}
	 * @param signature {@link OperationSignature} the {@link SEFFPCMVertex} elements must start
	 * @return Returns all {@link SEFFPCMVertex} that are {@link StartAction} elements which start the implementation of the given {@link OperationSignature}
	 */
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

	/**
	 * Returns a list of {@link PCMTransposeFlowGraph} that contain the given {@link AbstractPCMVertex}
	 * @param element {@link AbstractPCMVertex} that must be contained in the {@link PCMTransposeFlowGraph}
	 * @return Returns a list of all {@link PCMTransposeFlowGraph} that must contain the {@link AbstractPCMVertex}
	 */
	public List<PCMTransposeFlowGraph> findTransposeFlowGraphsWithElement(AbstractPCMVertex<?> element) {
		return flowGraphs.getTransposeFlowGraphs().stream()
				.filter(PCMTransposeFlowGraph.class::isInstance)
				.filter(it -> it.getVertices().contains(element))
				.map(PCMTransposeFlowGraph.class::cast)
				.toList();
	}

	/**
	 * Determines a list of all possible {@link AssemblyContext} states that occur in all transpose flow graphs
	 * @return Returns a collection containing all {@link AssemblyContext} states in the transpose flow graphs
	 */
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

	/**
	 * Finds a list of {@link AbstractPCMVertex} that match the given {@link UsageScenario} or {@link ResourceContainer}
	 * @param actor {@link UsageScenario} or {@link ResourceContainer} the {@link AbstractPCMVertex} must match
	 * @return Returns a list of {@link AbstractPCMVertex} that match the given {@link UsageScenario} or {@link ResourceContainer}
	 */
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

	/**
	 * Looks up a given PCM model with the required {@link EClass} and of type {@link T}
	 * @param eclazz {@link EClass} of the PCM model
	 * @param clazz Class of the PCM model element
	 * @return Returns a PCM model element of the given class
	 * @throws IllegalStateException Thrown if the specified model cannot be found
	 * @param <T> Type parameter that describes the class of the returned element
	 */
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

	/**
	 * Returns the {@link Repository} model of the contained elements
	 * @return Returns the {@link Repository} model of the contained elements
	 */
	private Repository lookupRepositoryModel() {
		return this.lookupPCMModel(RepositoryPackage.eINSTANCE.getRepository(), Repository.class);
	}

	/**
	 * Returns the {@link System} model of the contained elements
	 * @return Returns the {@link System} model of the contained elements
	 */
	private System lookupSystemModel() {
		return this.lookupPCMModel(SystemPackage.eINSTANCE.getSystem(), System.class);
	}

	/**
	 * Returns the {@link ResourceEnvironment} model of the contained elements
	 * @return Returns the {@link ResourceEnvironment} model of the contained elements
	 */
	private ResourceEnvironment lookupResourceEnvironmentModel() {
		return this.lookupPCMModel(ResourceenvironmentPackage.eINSTANCE.getResourceEnvironment(), ResourceEnvironment.class);
	}

	/**
	 * Returns the {@link Allocation} model of the contained elements
	 * @return Returns the {@link Allocation} model of the contained elements
	 */
	private Allocation lookupAllocationModel() {
		return this.resourceProvider.getAllocation();
	}

	/**
	 * Returns the {@link UsageModel} of the contained elements
	 * @return Returns the {@link UsageModel} of the contained elements
	 */
	private UsageModel lookupUsageModel() {
		return this.resourceProvider.getUsageModel();
	}

	/**
	 * Finds a list of {@link StartAction} elements of an {@link BranchAction} with the given ID
	 * @param id ID of the {@link BranchAction}
	 * @return Returns a list of {@link StartAction} elements with a parent {@link BranchAction} with the required ID
	 */
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

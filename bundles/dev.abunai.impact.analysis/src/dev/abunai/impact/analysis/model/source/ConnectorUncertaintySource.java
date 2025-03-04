package dev.abunai.impact.analysis.model.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.OperationInterface;

import dev.abunai.impact.analysis.model.impact.ConnectorUncertaintyImpact;
import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents a source of uncertainty on an {@link AssemblyConnector} or {@link ProvidedDelegationConnector}
 * @param <T> Type parameter of the affected element
 */
public class ConnectorUncertaintySource<T extends Connector> extends UncertaintySource<T> {
	private final T connector;
	private final PropagationHelper propagationHelper;

	/**
	 * Create a new {@link ConnectorUncertaintySource} with the given affected connector element
	 * @param connector Affected action {@link Entity} object
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 */
	private ConnectorUncertaintySource(T connector, PropagationHelper propagationHelper) {
		Objects.requireNonNull(connector);
		Objects.requireNonNull(propagationHelper);
		this.connector = connector;
		this.propagationHelper = propagationHelper;
	}

	/**
	 * Create a new {@link ConnectorUncertaintySource} with the given affected action element
	 * @param connector Affected action {@link Entity} object
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 * @return Returns a new {@link ConnectorUncertaintySource} with the given connector {@link Entity}
	 */
	public static ConnectorUncertaintySource<? extends Connector> of(Connector connector,
			PropagationHelper propagationHelper) {
		if (connector instanceof AssemblyConnector) {
			return new ConnectorUncertaintySource<>((AssemblyConnector) connector, propagationHelper);
		} else if (connector instanceof ProvidedDelegationConnector) {
			return new ConnectorUncertaintySource<>((ProvidedDelegationConnector) connector,
                    propagationHelper);
		} else {
			throw new IllegalStateException("Unrecognized connector type");
		}
	}

	/**
	 * Determines the {@link OperationInterface} that the targeted connector targets
	 * @return Returns the {@link OperationInterface} the affected connector targets
	 */
	private OperationInterface getConnectorInterface() {
		if (this.connector instanceof AssemblyConnector castedConnector) {
			return castedConnector.getProvidedRole_AssemblyConnector().getProvidedInterface__OperationProvidedRole();
		} else if (this.connector instanceof ProvidedDelegationConnector castedConnector) {
			return castedConnector.getInnerProvidedRole_ProvidedDelegationConnector()
					.getProvidedInterface__OperationProvidedRole();
		} else {
			throw new IllegalStateException("Unrecognized connector type");
		}
	}


	/**
	 * Determines the {@link AssemblyContext} that the targeted connector provides
	 * @return Returns the providing {@link AssemblyContext} of the targeted connectors
	 */
	private AssemblyContext getConnectorProvidingContext() {
		if (connector instanceof AssemblyConnector castedConnector) {
			return castedConnector.getProvidingAssemblyContext_AssemblyConnector();
		} else if (connector instanceof ProvidedDelegationConnector castedConnector) {
			return castedConnector.getAssemblyContext_ProvidedDelegationConnector();
		} else {
			throw new IllegalStateException("Unrecognized connector type.");
		}
	}

	@Override
	public T getArchitecturalElement() {
		return connector;
	}

	@Override
	public List<? extends UncertaintyImpact<? extends T>> propagate() {
		List<AbstractPCMVertex<?>> matches = new ArrayList<>();

		OperationInterface interfaze = getConnectorInterface();
		AssemblyContext providingContext = getConnectorProvidingContext();

		var startNodes = propagationHelper.findStartActionsOfSEFFsThatImplement(interfaze);
		var systemCallNodes = propagationHelper.findEntryLevelSystemCallsViaInterface(interfaze);
		var externalCallNodes = propagationHelper.findExternalCallsViaInterface(interfaze);

		if (!startNodes.isEmpty()) {
			var filteredStartNodes = startNodes.stream().filter(it -> it.getContext().contains(providingContext))
					.toList();
			matches.addAll(filteredStartNodes);
		}

		if (!systemCallNodes.isEmpty() && connector instanceof ProvidedDelegationConnector castedConnector) {
			var filteredSystemCallNodes = systemCallNodes.stream()
					.filter(it -> it.getReferencedElement().getProvidedRole_EntryLevelSystemCall()
							.equals(castedConnector.getOuterProvidedRole_ProvidedDelegationConnector()))
					.toList();
			matches.addAll(filteredSystemCallNodes);
		}

		if (!externalCallNodes.isEmpty() && connector instanceof AssemblyConnector castedConnector) {
			var filteredExternalCallNodes = externalCallNodes.stream().filter(it -> it.getReferencedElement()
					.getRole_ExternalService().equals(castedConnector.getRequiredRole_AssemblyConnector())).toList();
			matches.addAll(filteredExternalCallNodes);
		}

		return matches.stream().map(it -> new ConnectorUncertaintyImpact<>(it, this, this.propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Connector";
	}
}

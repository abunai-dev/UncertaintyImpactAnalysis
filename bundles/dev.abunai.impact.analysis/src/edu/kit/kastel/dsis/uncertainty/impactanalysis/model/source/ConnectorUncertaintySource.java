package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationInterface;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.ConnectorUncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact.UncertaintyImpact;
import edu.kit.kastel.dsis.uncertainty.impactanalysis.util.PropagationHelper;

public class ConnectorUncertaintySource<T extends Connector> extends UncertaintySource<T> {

	private final T connector;
	private final PropagationHelper propagationHelper;

	private ConnectorUncertaintySource(T connector, PropagationHelper propagationHelper) {
		Objects.requireNonNull(connector);
		Objects.requireNonNull(propagationHelper);
		this.connector = connector;
		this.propagationHelper = propagationHelper;
	}

	public static ConnectorUncertaintySource<? extends Connector> of(Connector connector,
			PropagationHelper propagationHelper) {
		if (connector instanceof AssemblyConnector) {
			return new ConnectorUncertaintySource<AssemblyConnector>((AssemblyConnector) connector, propagationHelper);
		} else if (connector instanceof ProvidedDelegationConnector) {
			return new ConnectorUncertaintySource<ProvidedDelegationConnector>((ProvidedDelegationConnector) connector,
					propagationHelper);
		} else {
			throw new IllegalStateException("Unrecognized connector type.");
		}
	}

	private OperationInterface getConnectorInterface() {
		if (connector instanceof AssemblyConnector castedConnector) {
			return castedConnector.getProvidedRole_AssemblyConnector().getProvidedInterface__OperationProvidedRole();
		} else if (connector instanceof ProvidedDelegationConnector castedConnector) {
			return castedConnector.getInnerProvidedRole_ProvidedDelegationConnector()
					.getProvidedInterface__OperationProvidedRole();
		} else {
			throw new IllegalStateException("Unrecognized connector type.");
		}
	}

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
		List<AbstractPCMActionSequenceElement<?>> matches = new ArrayList<>();

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
					.filter(it -> it.getElement().getProvidedRole_EntryLevelSystemCall()
							.equals(castedConnector.getOuterProvidedRole_ProvidedDelegationConnector()))
					.toList();
			matches.addAll(filteredSystemCallNodes);
		}

		if (!externalCallNodes.isEmpty() && connector instanceof AssemblyConnector castedConnector) {
			var filteredExternalCallNodes = externalCallNodes.stream().filter(it -> it.getElement()
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

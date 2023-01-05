package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;

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

	@Override
	public T getArchitecturalElement() {
		return connector;
	}

	@Override
	public List<? extends UncertaintyImpact<? extends T>> propagate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUncertaintyType() {
		return "Connector";
	}

}

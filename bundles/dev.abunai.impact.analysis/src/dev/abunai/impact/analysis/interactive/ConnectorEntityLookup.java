package dev.abunai.impact.analysis.interactive;

import java.util.List;
import java.util.ArrayList;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

/**
 * Looks up elements relevant for the connector {@link ArchitecturalElementType}
 */
public class ConnectorEntityLookup extends EntityLookup {
	private final List<AssemblyConnector> assemblyConnectors;
	private final List<ProvidedDelegationConnector> providedDelegationConnector;

	/**
	 * Create a new {@link ComponentEntityLookup} with the given impact analysis.
	 * It looks up the elements relevant for the connector {@link ArchitecturalElementType}
	 * @param analysis PCM Impact analysis used to lookup elements
	 */
	public ConnectorEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		this.assemblyConnectors = this.findAllElementsOfType(CompositionPackage.eINSTANCE.getAssemblyConnector(), AssemblyConnector.class);
		this.providedDelegationConnector = this.findAllElementsOfType(CompositionPackage.eINSTANCE.getProvidedDelegationConnector(), ProvidedDelegationConnector.class);
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> result = new ArrayList<>();
		result.addAll(this.assemblyConnectors);
		result.addAll(this.providedDelegationConnector);
		return result;
	}

	@Override
	public void addToAnalysis(int index) {
		if (index < this.assemblyConnectors.size()) {
			analysis.getUncertaintySources().addConnectorUncertaintyInConnector(assemblyConnectors.get(index).getId());
		} else {
			analysis.getUncertaintySources().addConnectorUncertaintyInConnector(providedDelegationConnector.get(index - assemblyConnectors.size()).getId());
		}
	}

}

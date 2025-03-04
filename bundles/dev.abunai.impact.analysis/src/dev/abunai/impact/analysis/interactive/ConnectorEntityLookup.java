package dev.abunai.impact.analysis.interactive;

import java.util.List;
import java.util.ArrayList;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

public class ConnectorEntityLookup extends EntityLookup {

	private final List<AssemblyConnector> assemblyConnectors;
	private final List<ProvidedDelegationConnector> providedDelegationConnector;
	
	public ConnectorEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		assemblyConnectors = findAllElementsOfType(CompositionPackage.eINSTANCE.getAssemblyConnector(), AssemblyConnector.class);
		providedDelegationConnector = findAllElementsOfType(CompositionPackage.eINSTANCE.getProvidedDelegationConnector(), ProvidedDelegationConnector.class);
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> result = new ArrayList<>();
		result.addAll(assemblyConnectors);
		result.addAll(providedDelegationConnector);
		return result;
	}

	@Override
	public void addToAnalysis(int index) {
		if (index < assemblyConnectors.size()) {
			analysis.getUncertaintySources().addConnectorUncertaintyInConnector(assemblyConnectors.get(index).getId());
		} else {
			analysis.getUncertaintySources().addConnectorUncertaintyInConnector(providedDelegationConnector.get(index - assemblyConnectors.size()).getId());
		}
	}

}

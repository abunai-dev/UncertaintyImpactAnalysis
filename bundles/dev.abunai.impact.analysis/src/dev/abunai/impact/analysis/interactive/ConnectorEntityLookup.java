package dev.abunai.impact.analysis.interactive;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

public class ConnectorEntityLookup extends EntityLookup {

	private final List<Connector> connectors;
	
	public ConnectorEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		connectors = findAllElementsOfType(CompositionPackage.eINSTANCE.getConnector(),Connector.class);
	}

	@Override
	public List<Entity> getEntities() {
		return connectors.stream().map(e -> (Entity)e).toList();
	}

	@Override
	public void addToAnalysis(int index) {
		analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext(connectors.get(index).getId());
	}

}

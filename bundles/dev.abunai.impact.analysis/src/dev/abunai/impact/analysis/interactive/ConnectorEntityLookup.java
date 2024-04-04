package dev.abunai.impact.analysis.interactive;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SeffPackage;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

public class ConnectorEntityLookup extends EntityLookup {

	private final List<ExternalCallAction> externalCalls;
	private final List<EntryLevelSystemCall> entryLevelSystemCalls;
	
	public ConnectorEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		externalCalls = findAllElementsOfType(SeffPackage.eINSTANCE.getExternalCallAction(), ExternalCallAction.class);
		entryLevelSystemCalls = findAllElementsOfType(UsagemodelPackage.eINSTANCE.getEntryLevelSystemCall(), EntryLevelSystemCall.class);
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> result = new ArrayList<>();
		result.addAll(externalCalls);
		result.addAll(entryLevelSystemCalls);
		return result;
	}

	@Override
	public void addToAnalysis(int index) {
		String id = null;
		if (index < externalCalls.size()) {
			id = externalCalls.get(index).getId();
		} else {
			id = entryLevelSystemCalls.get(index - externalCalls.size()).getId();
		}
		analysis.getUncertaintySources().addConnectorUncertaintyInConnector(id);
		
	}

}

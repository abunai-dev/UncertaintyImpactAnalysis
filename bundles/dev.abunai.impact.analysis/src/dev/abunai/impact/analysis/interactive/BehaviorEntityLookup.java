package dev.abunai.impact.analysis.interactive;

import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.SeffPackage;
import org.palladiosimulator.pcm.seff.SetVariableAction;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

public class BehaviorEntityLookup extends EntityLookup {

	private final List<SetVariableAction> setVariableActions;
	
	public BehaviorEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		setVariableActions = findAllElementsOfType(SeffPackage.eINSTANCE.getSetVariableAction(), SetVariableAction.class);
	}

	@Override
	public List<Entity> getEntities() {
		return setVariableActions.stream().map(e -> (Entity)e).toList();
	}

	@Override
	public void addToAnalysis(int index) {
		analysis.getUncertaintySources().addBehaviorUncertaintyInSetVariableAction(setVariableActions.get(index).getId());
	}

}

package dev.abunai.impact.analysis.interactive;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SeffPackage;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

/**
 * Looks up elements relevant for the behavior {@link ArchitecturalElementType}
 */
public class BehaviorEntityLookup extends EntityLookup {
	private final List<ExternalCallAction> externalCalls;
	private final List<EntryLevelSystemCall> entryLevelSystemCalls;
	private final List<SetVariableAction> setVariableActions;
	private final List<BranchAction> branchActions;

	/**
	 * Create a new {@link BehaviorEntityLookup} with the given impact analysis.
	 * It looks up the elements relevant for the behavior {@link ArchitecturalElementType}
	 * @param analysis PCM Impact analysis used to lookup elements
	 */
	public BehaviorEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		this.externalCalls = this.findAllElementsOfType(SeffPackage.eINSTANCE.getExternalCallAction(), ExternalCallAction.class);
		this.entryLevelSystemCalls = this.findAllElementsOfType(UsagemodelPackage.eINSTANCE.getEntryLevelSystemCall(), EntryLevelSystemCall.class);
		this.setVariableActions = this.findAllElementsOfType(SeffPackage.eINSTANCE.getSetVariableAction(), SetVariableAction.class);
		this.branchActions = this.findAllElementsOfType(SeffPackage.eINSTANCE.getBranchAction(), BranchAction.class);
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> result = new ArrayList<>();
		result.addAll(externalCalls);
		result.addAll(entryLevelSystemCalls);
		result.addAll(setVariableActions);
		result.addAll(branchActions);
		return result;
	}

	@Override
	public void addToAnalysis(int index) {
		int currentIndex = index;
		if (currentIndex < externalCalls.size()) {
			analysis.getUncertaintySources().addBehaviorUncertaintyInExternalCallAction(externalCalls.get(currentIndex).getId());
			return;
		}
		currentIndex -= externalCalls.size();
		
		if (currentIndex < entryLevelSystemCalls.size()) {
			analysis.getUncertaintySources().addBehaviorUncertaintyInEntryLevelSystemCall(entryLevelSystemCalls.get(currentIndex).getId());
			return;
		}
		currentIndex -= entryLevelSystemCalls.size();
		
		if (currentIndex < setVariableActions.size()) {
			analysis.getUncertaintySources().addBehaviorUncertaintyInSetVariableAction(setVariableActions.get(currentIndex).getId());
			return;
		}
		currentIndex -= setVariableActions.size();
		
		analysis.getUncertaintySources().addBehaviorUncertaintyInBranch(branchActions.get(currentIndex).getId());
		
	}

}

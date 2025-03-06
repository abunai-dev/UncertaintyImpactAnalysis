package dev.abunai.impact.analysis.webview;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;
import dev.abunai.impact.analysis.webview.jsonmodel.ScenarioBehaviourJson;
import dev.abunai.impact.analysis.webview.jsonmodel.seff.*;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;

class SeffTransformer implements AbstractTransformer<Entity> {

	@Override
	public ActionJson transform(Entity action) {
		if (action instanceof Start) {
			return new StartActionJson(action.getId(), getId(((Start)action).getSuccessor()));
		}
		if (action instanceof StartAction) {
			return new StartActionJson(action.getId(), getId(((StartAction)action).getSuccessor_AbstractAction()));
		}
		
		if (action instanceof Stop) {
			return new StopActionJson(action.getId(), getId(((Stop)action).getSuccessor()));
		}
		if (action instanceof StopAction) {
			return new StopActionJson(action.getId(), getId(((StopAction)action).getSuccessor_AbstractAction()));
		}
		
		if (action instanceof EntryLevelSystemCall) {
			EntryLevelSystemCall node = (EntryLevelSystemCall)action;
			return new EntryLevelSystemCallJson(node.getId(), getId(node.getSuccessor()), node.getEntityName(), 
					node.getInputParameterUsages_EntryLevelSystemCall().stream().map(v -> transformVariableUsage(v)).toList(), 
					node.getOutputParameterUsages_EntryLevelSystemCall().stream().map(v -> transformVariableUsage(v)).toList());
		}
		if (action instanceof ExternalCallAction) {
			ExternalCallAction node = (ExternalCallAction)action;
			return new ExternalCallJson(node.getId(), getId(node.getSuccessor_AbstractAction()), node.getEntityName(), 
					node.getInputVariableUsages__CallAction().stream().map(v -> transformVariableUsage(v)).toList(), 
					node.getReturnVariableUsage__CallReturnAction().stream().map(v -> transformVariableUsage(v)).toList());
		}
		
		if (action instanceof BranchAction) {
			BranchAction branchAction = (BranchAction)action;
			return new BranchJson(branchAction.getId(), getId(branchAction.getSuccessor_AbstractAction()), branchAction.getEntityName(), 
					branchAction.getBranches_Branch().stream().map(t -> transformBranchTransiton(t)).toList());
		}
		if (action instanceof Branch) {
			Branch branch = (Branch)action;
			return new BranchJson(branch.getId(), getId(branch.getSuccessor()), branch.getEntityName(), 
					branch.getBranchTransitions_Branch().stream().map(t -> transformTransition(t)).toList());
		}
		
		if (action instanceof SetVariableAction) {
			SetVariableAction node = (SetVariableAction)action;
			return new SetVariableJson(node.getId(), getId(node.getSuccessor_AbstractAction()), node.getEntityName(),
					node.getLocalVariableUsages_SetVariableAction().stream().map(v -> transformVariableUsage(v)).toList());
		}
		
		if (action instanceof AbstractUserAction) {
			AbstractUserAction node = (AbstractUserAction)action;
			return new UnconcreteAction(node.getId(), getId(node.getSuccessor()), node.getEntityName(), node.getClass().getName());
		}
		if (action instanceof AbstractAction) {
			AbstractAction node = (AbstractAction)action;
			return new UnconcreteAction(node.getId(), getId(node.getSuccessor_AbstractAction()), node.getEntityName(), node.getClass().getName());
		}
		throw new RuntimeException("Could not identify type of action");
	}
	
	String getId(Entity e) {
		if (e == null) return null;
		return e.getId();
				
	}

	
	public VariableUsageJson transformVariableUsage(VariableUsage variableUsage) {
		return new VariableUsageJson(Util.generateUUID(), variableUsage.getNamedReference__VariableUsage().getReferenceName());
	}
	
	private JsonObject transformBranchTransiton(AbstractBranchTransition transition) {
		if (transition instanceof ProbabilisticBranchTransition) {
			ProbabilisticBranchTransition branchTransition = (ProbabilisticBranchTransition)transition;
			
			return new ProbabilisticTransitionJson(branchTransition.getId(), branchTransition.getEntityName(), branchTransition.getBranchProbability(),
					branchTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().stream().map(a -> transform(a)).toList());
		}
		
		if (transition instanceof GuardedBranchTransition) {
			GuardedBranchTransition branchTransition = (GuardedBranchTransition)transition;
			return new GuardedTransitionJson(branchTransition.getId(), branchTransition.getEntityName(),
					branchTransition.getBranchCondition_GuardedBranchTransition().getExpression().toString(),
					branchTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour().stream().map(a -> transform(a)).toList());
		}
		
		throw new RuntimeException("Could not identify type of Branch Transition");
	}
	
	private JsonObject transformTransition(BranchTransition transition) {
		ScenarioBehaviour behaviour = transition.getBranchedBehaviour_BranchTransition();
		ScenarioBehaviourJson behaviourJson = new ScenarioBehaviourJson(behaviour.getId(), "", 
				behaviour.getActions_ScenarioBehaviour().stream().map(a -> transform(a)).toList());
		
		return new BranchTransitionJson(Util.generateUUID(), transition.getBranchProbability(), behaviourJson);
	}
}

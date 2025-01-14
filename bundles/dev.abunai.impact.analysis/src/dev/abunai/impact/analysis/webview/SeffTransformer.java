package dev.abunai.impact.analysis.webview;

import java.util.List;

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
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;

public class SeffTransformer implements AbstractTransformer<Entity> {

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
}

class TransitionJson extends JsonObject {
	public String name;
	public List<ActionJson> actions;
	
	protected TransitionJson(String id, String type, String name, List<ActionJson> actions) {
		super(id, type);
		this.name = name;
		this.actions = actions;
	}
	
}

class ProbabilisticTransitionJson extends TransitionJson {
	public double probability;

	public ProbabilisticTransitionJson(String id, String name, double probability, List<ActionJson> actions) {
		super(id, "ProbabilisticBranchTransition", name, actions);
		this.probability = probability;
		
	}	
}

class GuardedTransitionJson extends TransitionJson {
	public String condition;

	public GuardedTransitionJson(String id, String name, String condition, List<ActionJson> actions) {
		super(id, "GuardedBranchTransition", name, actions);
		this.condition = condition;
		
	}	
}

class BranchJson extends ActionJson {
	public String name;
	public List<JsonObject> transitions;
	
	public BranchJson(String id, String successor, String name, List<JsonObject> transitions) {
		super(id, "Branch", successor);
		this.transitions = transitions;
		this.name = name;
	}
}

class VariableUsageJson extends JsonObject {
	public String referenceName;
	public VariableUsageJson(String id, String referenceName) {
		super(id, "VariableUsage");
		this.referenceName = referenceName;
	}
}

class EntryLevelSystemCallJson extends ActionJson {
	public String name;
	public List<VariableUsageJson> inputParameterUsages;
	public List<VariableUsageJson> outputParameterUsages;
	
	public EntryLevelSystemCallJson(String id, String successor, String name, List<VariableUsageJson> inputParameterUsages,
			List<VariableUsageJson> outputParameterUsages) {
		super(id, "EntryLevelSystemCall", successor);
		this.name = name;
		this.inputParameterUsages = inputParameterUsages;
		this.outputParameterUsages = outputParameterUsages;
	}
}

class ExternalCallJson extends ActionJson {
	public String name;
	public List<VariableUsageJson> inputParameterUsages;
	public List<VariableUsageJson> outputParameterUsages;
	
	public ExternalCallJson(String id, String successor, String name, List<VariableUsageJson> inputParameterUsages,
			List<VariableUsageJson> outputParameterUsages) {
		super(id, "ExternalCall", successor);
		this.name = name;
		this.inputParameterUsages = inputParameterUsages;
		this.outputParameterUsages = outputParameterUsages;
	}
}

class SetVariableJson extends ActionJson {
	public String name;
	public List<VariableUsageJson> variableUsages;
	
	public SetVariableJson(String id, String successor, String name, List<VariableUsageJson> variableUsages) {
		super(id, "SetVariable", successor);
		this.name = name;
		this.variableUsages = variableUsages;
	}
}

class StopActionJson extends ActionJson {

	public StopActionJson(String id, String successor) {
		super(id, "Stop", successor);
	}
	
}

class StartActionJson extends ActionJson {

	public StartActionJson(String id, String successor) {
		super(id, "Start", successor);
	}
	
}

class UnconcreteAction extends ActionJson {
	public String typeName;
	public String name;
	
	public UnconcreteAction(String id, String successor, String name, String typeName) {
		super(id, "AbstractAction", successor);
		this.name = name;
		this.typeName = typeName;
	}
}

abstract class ActionJson extends JsonObject {

	public String successor;
	
	protected ActionJson(String id, String type, String successor) {
		super(id, type);
		if (successor != null) {
			this.successor = successor;
		}
	}
	
}
package dev.abunai.impact.analysis.webview;

import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
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
		return null;
		//throw new RuntimeException("Could not identify type");
	}
	
	String getId(Entity e) {
		if (e == null) return null;
		return e.getId();
				
	}

	
	public VariableUsageJson transformVariableUsage(VariableUsage variableUsage) {
		return new VariableUsageJson(Util.generateUUID(), variableUsage.getNamedReference__VariableUsage().getReferenceName());
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

abstract class ActionJson extends JsonObject {

	public String successor;
	
	protected ActionJson(String id, String type, String successor) {
		super(id, type);
		if (successor != null) {
			this.successor = successor;
		}
	}
	
}
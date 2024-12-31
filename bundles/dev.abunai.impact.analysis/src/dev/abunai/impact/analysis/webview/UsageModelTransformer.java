package dev.abunai.impact.analysis.webview;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public class UsageModelTransformer implements AbstractTransformer<UsageModel> {

	@Override
	public JsonObject transform(UsageModel usageModel) {
		List<UsageScenarioJson> contents = new ArrayList<>();
		SeffTransformer seffTransformer = new SeffTransformer();
		for (UsageScenario usageScenario : usageModel.getUsageScenario_UsageModel()) {
			List<ScenarioBehaviourJson> scenarioBehaviourJson = new ArrayList<>();
			for (var c : usageScenario.eContents()) {
				if (c instanceof ScenarioBehaviour) {
					ScenarioBehaviour scenarioBehaviour = (ScenarioBehaviour)c;
					scenarioBehaviourJson.add(new ScenarioBehaviourJson(scenarioBehaviour.getId(), scenarioBehaviour.getEntityName(), 
							scenarioBehaviour.getActions_ScenarioBehaviour().stream().map(a -> seffTransformer.transform(a)).toList()));
				}
			}
			contents.add(new UsageScenarioJson(usageScenario.getId(), usageScenario.getEntityName(), scenarioBehaviourJson));
		}
		return new UsageModelJson(Util.generateUUID(), contents);
	}
}

class UsageModelJson extends JsonObject {

	public List<UsageScenarioJson> contents;
	
	
	public UsageModelJson(String id, List<UsageScenarioJson> contents) {
		super(id, "UsageModel");
		this.contents = contents;
	}
	
}

class UsageScenarioJson extends JsonObject {
	public String name;
	public List<ScenarioBehaviourJson> contents;
	
	public UsageScenarioJson(String id, String name, List<ScenarioBehaviourJson> contents) {
		super(id, "UsageScenario");
		this.name = name;
		this.contents = contents;
	}
}

class ScenarioBehaviourJson extends JsonObject {
	public String name;
	public List<ActionJson> contents;
	
	public ScenarioBehaviourJson(String id, String name, List<ActionJson> contents) {
		super(id, "ScenarioBehaviour");
		this.name = name;
		this.contents = contents;
	}
}
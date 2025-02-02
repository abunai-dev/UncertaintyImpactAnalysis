package dev.abunai.impact.analysis.webview;

import java.util.ArrayList;
import java.util.List;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;
import dev.abunai.impact.analysis.webview.jsonmodel.ScenarioBehaviourJson;
import dev.abunai.impact.analysis.webview.jsonmodel.UsageModelJson;
import dev.abunai.impact.analysis.webview.jsonmodel.UsageScenarioJson;
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

package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class UsageScenarioJson extends JsonObject {
    public String name;
    public List<ScenarioBehaviourJson> contents;

    public UsageScenarioJson(String id, String name, List<ScenarioBehaviourJson> contents) {
        super(id, "UsageScenario");
        this.name = name;
        this.contents = contents;
    }
}

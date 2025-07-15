package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class UsageModelJson extends JsonObject {

    public List<UsageScenarioJson> contents;


    public UsageModelJson(String id, List<UsageScenarioJson> contents) {
        super(id, "UsageModel");
        this.contents = contents;
    }

}

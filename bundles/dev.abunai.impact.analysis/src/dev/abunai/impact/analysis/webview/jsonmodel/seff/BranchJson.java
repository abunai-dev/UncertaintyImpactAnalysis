package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;

import java.util.List;

public class BranchJson extends ActionJson {
    public String name;
    public List<JsonObject> transitions;

    public BranchJson(String id, String successor, String name, List<JsonObject> transitions) {
        super(id, "Branch", successor);
        this.transitions = transitions;
        this.name = name;
    }
}

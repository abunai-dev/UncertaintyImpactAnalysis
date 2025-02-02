package dev.abunai.impact.analysis.webview.jsonmodel;

import dev.abunai.impact.analysis.webview.jsonmodel.seff.ActionJson;

import java.util.List;

public class ScenarioBehaviourJson extends JsonObject {
    public String name;
    public List<ActionJson> contents;

    public ScenarioBehaviourJson(String id, String name, List<ActionJson> contents) {
        super(id, "ScenarioBehaviour");
        this.name = name;
        this.contents = contents;
    }
}
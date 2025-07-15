package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;

import java.util.List;

public class TransitionJson extends JsonObject {
    public String name;
    public List<ActionJson> actions;

    protected TransitionJson(String id, String type, String name, List<ActionJson> actions) {
        super(id, type);
        this.name = name;
        this.actions = actions;
    }

}

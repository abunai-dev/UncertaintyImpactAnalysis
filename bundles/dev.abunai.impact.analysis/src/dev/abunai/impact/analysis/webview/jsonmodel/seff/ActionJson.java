package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;

public abstract class ActionJson extends JsonObject {

    public String successor;

    protected ActionJson(String id, String type, String successor) {
        super(id, type);
        if (successor != null) {
            this.successor = successor;
        }
    }

}

package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;

public class VariableUsageJson extends JsonObject {
    public String referenceName;
    public VariableUsageJson(String id, String referenceName) {
        super(id, "VariableUsage");
        this.referenceName = referenceName;
    }
}

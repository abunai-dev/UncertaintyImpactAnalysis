package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import java.util.List;

public class SetVariableJson extends ActionJson {
    public String name;
    public List<VariableUsageJson> variableUsages;

    public SetVariableJson(String id, String successor, String name, List<VariableUsageJson> variableUsages) {
        super(id, "SetVariable", successor);
        this.name = name;
        this.variableUsages = variableUsages;
    }
}

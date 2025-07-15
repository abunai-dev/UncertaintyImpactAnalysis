package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import java.util.List;

public class EntryLevelSystemCallJson extends ActionJson {
    public String name;
    public List<VariableUsageJson> inputParameterUsages;
    public List<VariableUsageJson> outputParameterUsages;

    public EntryLevelSystemCallJson(String id, String successor, String name, List<VariableUsageJson> inputParameterUsages,
                                    List<VariableUsageJson> outputParameterUsages) {
        super(id, "EntryLevelSystemCall", successor);
        this.name = name;
        this.inputParameterUsages = inputParameterUsages;
        this.outputParameterUsages = outputParameterUsages;
    }
}

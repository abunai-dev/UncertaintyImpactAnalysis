package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import java.util.List;

public class ExternalCallJson extends ActionJson {
    public String name;
    public List<VariableUsageJson> inputParameterUsages;
    public List<VariableUsageJson> outputParameterUsages;

    public ExternalCallJson(String id, String successor, String name, List<VariableUsageJson> inputParameterUsages,
                            List<VariableUsageJson> outputParameterUsages) {
        super(id, "ExternalCall", successor);
        this.name = name;
        this.inputParameterUsages = inputParameterUsages;
        this.outputParameterUsages = outputParameterUsages;
    }
}

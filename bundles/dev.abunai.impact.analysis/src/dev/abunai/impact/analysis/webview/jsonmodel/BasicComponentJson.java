package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class BasicComponentJson extends JsonObject {
    public String name;
    public List<SeffJson> seffs;
    public List<ComponentInterfaceConnection> required;
    public List<ComponentInterfaceConnection> provided;


    public BasicComponentJson(String id, String name, List<SeffJson> seffs, List<ComponentInterfaceConnection> required, List<ComponentInterfaceConnection> provided) {
        super(id, "BasicComponent");
        this.name = name;
        this.seffs = seffs;
        this.required = required;
        this.provided = provided;
    }
}

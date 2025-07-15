package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class InterfaceJson extends JsonObject {
    public String name;
    public List<String> signatures;

    public InterfaceJson(String id, String name, List<String> signatures) {
        super(id, "Interface");
        this.name = name;
        this.signatures = signatures;
    }
}

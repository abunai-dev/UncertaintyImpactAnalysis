package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class CompositeDataTypeJson extends JsonObject {
    public String name;
    public List<String> signatures;
    public List<String> contained;

    public CompositeDataTypeJson(String id, String name, List<String> signatures, List<String> contained) {
        super(id, "CompositeDataType");
        this.name = name;
        this.signatures = signatures;
        this.contained = contained;
    }
}

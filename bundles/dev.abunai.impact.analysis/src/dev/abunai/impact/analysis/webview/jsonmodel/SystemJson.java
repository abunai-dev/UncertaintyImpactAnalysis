package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class SystemJson extends JsonObject {
    public String name;
    public List<JsonObject> contents;

    public SystemJson(String id, String name, List<JsonObject> contents) {
        super(id, "System");
        this.contents = contents;
    }
}
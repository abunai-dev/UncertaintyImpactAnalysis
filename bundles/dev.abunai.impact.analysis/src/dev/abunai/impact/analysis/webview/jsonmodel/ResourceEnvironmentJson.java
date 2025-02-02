package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class ResourceEnvironmentJson extends JsonObject {
    public List<JsonObject> contents;

    public ResourceEnvironmentJson(String id, List<JsonObject> contents) {
        super(id, "ResourceEnvironment");
        this.contents = contents;
    }
}

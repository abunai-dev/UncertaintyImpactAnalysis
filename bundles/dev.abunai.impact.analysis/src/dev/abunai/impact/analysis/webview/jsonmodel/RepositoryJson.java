package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class RepositoryJson extends JsonObject {
    public List<JsonObject> contents;


    public RepositoryJson(String id, List<JsonObject> contents) {
        super(id, "Repository");
        this.contents = contents;
    }
}

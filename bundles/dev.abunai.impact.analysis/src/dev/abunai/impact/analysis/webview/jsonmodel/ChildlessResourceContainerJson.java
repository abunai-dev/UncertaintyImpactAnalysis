package dev.abunai.impact.analysis.webview.jsonmodel;

public class ChildlessResourceContainerJson extends JsonObject {
    public String name;

    public ChildlessResourceContainerJson(String name, String id) {
        super(id, "ResourceContainer");
        this.name = name;
    }
}

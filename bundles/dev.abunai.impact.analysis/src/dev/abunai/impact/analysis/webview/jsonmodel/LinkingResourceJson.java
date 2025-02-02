package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;

public class LinkingResourceJson extends JsonObject {
    public String name;
    public List<String> connectedResourceContainer;

    public LinkingResourceJson(String name, String id, List<String> connectedResourceContainer) {
        super(id, "LinkingResource");
        this.name = name;
        this.connectedResourceContainer = connectedResourceContainer;
    }
}

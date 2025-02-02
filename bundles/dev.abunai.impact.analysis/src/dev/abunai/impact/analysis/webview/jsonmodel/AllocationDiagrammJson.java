package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;
public class AllocationDiagrammJson extends JsonObject {
    public List<ResourceContainerJson> contents;

    public AllocationDiagrammJson(String id, List<ResourceContainerJson> contents) {
        super(id, "AllocationDiagramm");
        this.contents = contents;
    }
}
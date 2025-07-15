package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;
import java.util.Objects;

public class ResourceContainerJson extends JsonObject {
    public String name;
    public List<AssemblyContextJson> contents;

    public ResourceContainerJson(String name, String id, List<AssemblyContextJson> contents) {
        super(id, "ResourceContainer");
        this.name = name;
        this.contents = contents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}

package dev.abunai.impact.analysis.webview.jsonmodel;

public class AssemblyContextJson extends JsonObject {
    public String name;

    public AssemblyContextJson(String name, String id) {
        super(id, "AssemblyContext");
        this.name = name;
    }
}

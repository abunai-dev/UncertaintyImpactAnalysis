package dev.abunai.impact.analysis.webview.jsonmodel;

public class OperationProvidedRoleJson extends JsonObject {
    public String name;
    public String providingRole;
    public OperationProvidedRoleJson(String name, String id, String providingRole) {
        super(id, "OperationProvided");
        this.name = name;
        this.providingRole = providingRole;
    }
}

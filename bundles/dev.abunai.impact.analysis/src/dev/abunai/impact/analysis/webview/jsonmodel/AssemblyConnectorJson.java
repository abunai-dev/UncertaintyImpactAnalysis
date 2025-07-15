package dev.abunai.impact.analysis.webview.jsonmodel;

public class AssemblyConnectorJson extends JsonObject {
    public String providingRole;
    public String requiredRole;
    public String providingAssebly;
    public String requiredAssembly;
    public AssemblyConnectorJson(String id, String providingRole, String requiredRole, String providingAssebly,
                                 String requiredAssembly) {
        super(id, "AssemblyConnector");
        this.providingRole = providingRole;
        this.requiredRole = requiredRole;
        this.providingAssebly = providingAssebly;
        this.requiredAssembly = requiredAssembly;
    }
}

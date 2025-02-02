package dev.abunai.impact.analysis.webview.jsonmodel;

public class ProvidedDelegationConnectorJson extends JsonObject {
    public String assemblyContext;
    public String outerProvidedRole;

    public ProvidedDelegationConnectorJson(String id, String assemblyContext, String outerProvidedRole) {
        super(id, "ProvidedDelegationConnector");
        this.assemblyContext = assemblyContext;
        this.outerProvidedRole = outerProvidedRole;
    }
}

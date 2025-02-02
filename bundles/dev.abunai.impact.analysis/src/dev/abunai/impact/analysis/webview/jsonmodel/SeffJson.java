package dev.abunai.impact.analysis.webview.jsonmodel;

import java.util.List;
import dev.abunai.impact.analysis.webview.jsonmodel.seff.ActionJson;

public class SeffJson extends JsonObject {
    public String signature;
    public List<ActionJson> actions;

    public SeffJson(String id, String signature, List<ActionJson> actions) {
        super(id, "Seff");
        this.signature = signature;
        this.actions = actions;
    }

}

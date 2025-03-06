package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;


public class BranchTransitionJson extends JsonObject {
	public double probability;
    public JsonObject behaviour;
    
    public BranchTransitionJson(String id, double probability, JsonObject behaviour) {
        super(id, "BranchTransition");
        this.behaviour = behaviour;
        this.probability = probability;
    }
}

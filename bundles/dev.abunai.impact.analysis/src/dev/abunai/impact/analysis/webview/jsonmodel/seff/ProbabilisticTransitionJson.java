package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import java.util.List;

public class ProbabilisticTransitionJson extends TransitionJson {
    public double probability;

    public ProbabilisticTransitionJson(String id, String name, double probability, List<ActionJson> actions) {
        super(id, "ProbabilisticBranchTransition", name, actions);
        this.probability = probability;

    }
}

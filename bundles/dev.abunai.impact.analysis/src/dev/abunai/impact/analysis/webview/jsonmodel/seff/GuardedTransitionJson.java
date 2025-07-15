package dev.abunai.impact.analysis.webview.jsonmodel.seff;

import java.util.List;

public class GuardedTransitionJson extends TransitionJson {
    public String condition;

    public GuardedTransitionJson(String id, String name, String condition, List<ActionJson> actions) {
        super(id, "GuardedBranchTransition", name, actions);
        this.condition = condition;

    }
}

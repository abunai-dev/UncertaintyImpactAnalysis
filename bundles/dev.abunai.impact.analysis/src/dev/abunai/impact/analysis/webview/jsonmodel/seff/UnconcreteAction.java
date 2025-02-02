package dev.abunai.impact.analysis.webview.jsonmodel.seff;

public class UnconcreteAction extends ActionJson {
    public String typeName;
    public String name;

    public UnconcreteAction(String id, String successor, String name, String typeName) {
        super(id, "AbstractAction", successor);
        this.name = name;
        this.typeName = typeName;
    }
}

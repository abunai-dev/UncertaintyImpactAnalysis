package dev.abunai.impact.analysis.webview;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;
import org.eclipse.emf.ecore.EObject;

public interface AbstractTransformer<T extends EObject> {

	public JsonObject transform(T v);
	
}

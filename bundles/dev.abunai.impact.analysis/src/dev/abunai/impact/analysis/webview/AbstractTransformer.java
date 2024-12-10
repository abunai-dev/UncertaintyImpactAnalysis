package dev.abunai.impact.analysis.webview;

import org.eclipse.emf.ecore.EObject;

public interface AbstractTransformer<T extends EObject> {

	public JsonObject transform(T v);
	
}

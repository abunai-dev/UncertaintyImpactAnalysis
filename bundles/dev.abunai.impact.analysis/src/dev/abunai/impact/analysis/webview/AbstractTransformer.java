package dev.abunai.impact.analysis.webview;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;
import org.eclipse.emf.ecore.EObject;

/**
 * Transforms a model type from PCM to the web format
 * @param <T> Type to transform
 */
interface AbstractTransformer<T extends EObject> {

	/**
	 * Transforms a PCM object to the web format
	 * @param v Object to transform
	 * @return The web representation of the given object
	 */
	public JsonObject transform(T v);
	
}

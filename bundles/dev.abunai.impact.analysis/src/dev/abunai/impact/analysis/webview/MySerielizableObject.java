package dev.abunai.impact.analysis.webview;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.palladiosimulator.pcm.PCMBaseClass;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.core.entity.NamedElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MySerielizableObject {

	@JsonProperty("name")
	private String name;
	@JsonProperty("id")
	private String id;
	@JsonProperty("type")
	private String type;
	@JsonProperty("children")
	private MySerielizableObject[] children;
	@JsonProperty("c1")
	private String c1;
	@JsonProperty("c2")
	private String c2;
	@JsonProperty("c3")
	private String c3;
	
	public MySerielizableObject(EObject object) {
		this(object, true);
	}
	
	public MySerielizableObject(EObject object, boolean doChildren) {
		name = buildName(object);
		
		id = object.eResource().getURIFragment(object);
		type = object.eClass().getName();
		if (doChildren) {
			children = new MySerielizableObject[object.eContents().size()];
			for (int i = 0; i < children.length; i++) {
				children[i] = new MySerielizableObject(object.eContents().get(i));
			}
		} else {
			children = new MySerielizableObject[0];
		}
		
		c1 = buildName(object.eContainer());
		c2 = buildName(object.eContainingFeature());
		c3 = buildName(object.eContainmentFeature());

		//object.eContents().stream().map(c -> new MySerielizableObject(c)).toArray(new MySerielizableObject[0]);
	}
	
	public String getName() {
		return name;
	}
	
	
	public String toString() {
		return name + " " + id + " " + type;
	}
	
	public static String buildName(EObject e) {
		if (e == null) return null;
		EStructuralFeature nameFeature = e.eClass().getEStructuralFeature("name");
		if (e instanceof NamedElement) {
			return ((NamedElement)e).getEntityName();
		}
		return null;
	}
}

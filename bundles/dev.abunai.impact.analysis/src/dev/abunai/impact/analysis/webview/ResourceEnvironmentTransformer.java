package dev.abunai.impact.analysis.webview;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

public class ResourceEnvironmentTransformer implements AbstractTransformer<ResourceEnvironment> {

	@Override
	public JsonObject transform(ResourceEnvironment resourceEnvironment) {
		List<JsonObject> contents = new ArrayList<>();
		for (var c : resourceEnvironment.eContents()) {
			if (c instanceof LinkingResource) {
				LinkingResource o = (LinkingResource)c;
				contents.add(new LinkingResourceJson(o.getEntityName(), o.getId(), 
						o.getConnectedResourceContainers_LinkingResource().stream().map(r -> r.getId()).toList()));
			}
			if (c instanceof ResourceContainer) {
				ResourceContainer o = (ResourceContainer)c;
				contents.add(new ChildlessResourceContainerJson(o.getEntityName(), o.getId()));
			}
		}
		return new ResourceEnvironmentJson(resourceEnvironment.getEntityName(), contents);
	}
}

class ResourceEnvironmentJson extends JsonObject {
	public List<JsonObject> contents;
	
	public ResourceEnvironmentJson(String id, List<JsonObject> contents) {
		super(id, "ResourceEnvironment");
		this.contents = contents;
	}
}

class LinkingResourceJson extends JsonObject {
	public String name;
	public List<String> connectedResourceContainer;
	
	public LinkingResourceJson(String name, String id, List<String> connectedResourceContainer) {
		super(id, "LinkingResource");
		this.name = name;
		this.connectedResourceContainer = connectedResourceContainer;
	}
}

class ChildlessResourceContainerJson extends JsonObject {
	public String name;
	
	public ChildlessResourceContainerJson(String name, String id) {
		super(id, "ResourceContainer");
		this.name = name;
	}
}
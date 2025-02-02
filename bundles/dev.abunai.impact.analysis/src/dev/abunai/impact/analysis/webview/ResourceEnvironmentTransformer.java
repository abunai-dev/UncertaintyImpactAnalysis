package dev.abunai.impact.analysis.webview;

import java.util.ArrayList;
import java.util.List;

import dev.abunai.impact.analysis.webview.jsonmodel.ChildlessResourceContainerJson;
import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;
import dev.abunai.impact.analysis.webview.jsonmodel.LinkingResourceJson;
import dev.abunai.impact.analysis.webview.jsonmodel.ResourceEnvironmentJson;
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

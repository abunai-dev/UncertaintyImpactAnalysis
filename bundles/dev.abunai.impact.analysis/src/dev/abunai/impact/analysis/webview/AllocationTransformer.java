package dev.abunai.impact.analysis.webview;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.abunai.impact.analysis.webview.jsonmodel.AllocationDiagrammJson;
import dev.abunai.impact.analysis.webview.jsonmodel.AssemblyContextJson;
import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;
import dev.abunai.impact.analysis.webview.jsonmodel.ResourceContainerJson;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;


public class AllocationTransformer implements AbstractTransformer<Allocation> {

	@Override
	public JsonObject transform(Allocation allocation) {
		Map<String, ResourceContainerJson> resourceContainer = new HashMap<>();
		List<ResourceContainerJson> contents = new ArrayList<>();
		
		for (var c : allocation.eContents()) {
			if (c instanceof AllocationContext) {
				var o = (AllocationContext) c;
				String containerID = o.getResourceContainer_AllocationContext().getId();
				if (!resourceContainer.containsKey(containerID)) {
					ResourceContainerJson container = new ResourceContainerJson(o.getResourceContainer_AllocationContext().getEntityName(), containerID, new ArrayList<>());
					contents.add(container);
					resourceContainer.put(containerID, container);
				}
				resourceContainer.get(containerID).contents.add(new AssemblyContextJson(o.getEntityName(), o.getId()));
			}
		}
		return new AllocationDiagrammJson(allocation.getId(), contents);
	}

}
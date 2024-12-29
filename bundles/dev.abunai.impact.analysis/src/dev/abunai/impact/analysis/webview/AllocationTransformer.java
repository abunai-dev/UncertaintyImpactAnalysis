package dev.abunai.impact.analysis.webview;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;

import com.google.common.base.Objects;

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


class AllocationDiagrammJson extends JsonObject {
	public List<ResourceContainerJson> contents;
	
	public AllocationDiagrammJson(String id, List<ResourceContainerJson> contents) {
		super(id, "AllocationDiagramm");
		this.contents = contents;
	}
}

class ResourceContainerJson extends JsonObject {
	public String name;
	public List<AssemblyContextJson> contents;
	
	public ResourceContainerJson(String name, String id, List<AssemblyContextJson> contents) {
		super(id, "ResourceContainer");
		this.name = name;
		this.contents = contents;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(name, id);
	}
}
package dev.abunai.impact.analysis.interactive;

import java.util.NoSuchElementException;

enum ArchitecturalElementType {
	
	//From https://github.com/abunai-dev/UncertaintySourceArchive/blob/main/UncertaintySourceArchive/src/model/categories/options/ArchitecturalElementTypeOptions.ts
	COMPONENT("Component"),
	CONNECTOR("Connector"),
	INTERFACE("Interface"),
	EXTERNAL_RESOURCE("External"),
	BEHAVIOR_DESCRIPTION("Behavior");

	private final String jsonName;
	
	private ArchitecturalElementType(String jsonName) {
		this.jsonName = jsonName;
	}
	
	public static ArchitecturalElementType getFromName(String jsonName) {
		for (ArchitecturalElementType element : ArchitecturalElementType.values()) {
			if (element.jsonName.equals(jsonName)) {
				return element;
			}
		}
		
		throw new NoSuchElementException(String.format("No element with name %s found.", jsonName));
	}
}

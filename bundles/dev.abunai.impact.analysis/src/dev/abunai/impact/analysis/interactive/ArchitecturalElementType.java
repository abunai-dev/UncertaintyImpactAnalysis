package dev.abunai.impact.analysis.interactive;

import java.util.NoSuchElementException;

/**
 * This enum represents types of architectural elements taken from the arc3n project.
 * They originate from <a href="https://github.com/abunai-dev/UncertaintySourceArchive/blob/main/UncertaintySourceArchive/src/model/categories/options/ArchitecturalElementTypeOptions.ts">...</a>
 */
enum ArchitecturalElementType {
	COMPONENT("Component"),
	CONNECTOR("Connector"),
	INTERFACE("Interface"),
	EXTERNAL_RESOURCE("External"),
	BEHAVIOR_DESCRIPTION("Behavior");

	private final String jsonName;

	/**
	 * Create a new {@link ArchitecturalElementType} from a given name in the .json
	 * @param jsonName name of the {@link ArchitecturalElementType} in the json
	 */
	ArchitecturalElementType(String jsonName) {
		this.jsonName = jsonName;
	}

	/**
	 * Get a {@link ArchitecturalElementType} from its name given a name
	 * @param jsonName Given name of the {@link ArchitecturalElementType} in the json file
	 * @throws NoSuchElementException Thrown if no element is present
	 * @return Returns the {@link ArchitecturalElementType} with the given name
	 */
	public static ArchitecturalElementType getFromName(String jsonName) {
		for (ArchitecturalElementType element : ArchitecturalElementType.values()) {
			if (element.jsonName.equals(jsonName)) {
				return element;
			}
		}
		throw new NoSuchElementException(String.format("No element with name %s found.", jsonName));
	}
}

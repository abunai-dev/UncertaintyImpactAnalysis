package dev.abunai.impact.analysis.interactive;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

record JsonUncertainty(@JsonProperty("id")int id, @JsonProperty("name")String name, @JsonProperty("classes")JsonUncertaintyClasses classes,
		@JsonProperty("keywords")List<String> keywords, @JsonProperty("description")String description, String exampleText, List<String> exampleImages,
		List<Integer> relatedUncertainties, List<Integer> children, int parent, String sourceReferenceLink) {
}

package dev.abunai.impact.analysis.interactive;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an uncertainty class from the arc3n json
 */
record JsonUncertaintyClasses(@JsonProperty("ArchitecturalElementType")String architecturalElementType,
	@JsonProperty("Location")String location,
	@JsonProperty("Type")String type,
	@JsonProperty("Manageability")String manageability,
	@JsonProperty("ResolutionTime")String resolutionTime,
	@JsonProperty("ReducibleByADD")String reducibleByADD,
	@JsonProperty("ImpactOnConfidentiality")String impactOnConfidentiality,
	@JsonProperty("SeverityOfTheImpact")String severityOfTheImpact) {
}

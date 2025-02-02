package dev.abunai.impact.analysis.webview.jsonmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JsonObject {
	@JsonProperty("type")
	protected final String type;
	@JsonProperty("id")
	protected final String id;
	
	protected JsonObject(String id, String type) {
		this.id = id;
		this.type = type;
	}
}

package dev.abunai.impact.analysis.webview;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SelectionData {
	@JsonProperty("component")
	public String component;
	@JsonProperty("uncertainty")
	public int uncertainty;
}

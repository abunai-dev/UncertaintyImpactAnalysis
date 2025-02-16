package dev.abunai.impact.analysis.webview;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data send by the viewer for the selection of components
 */
class SelectionData {
	@JsonProperty("component")
	public String component;
	@JsonProperty("uncertainty")
	public int uncertainty;
}

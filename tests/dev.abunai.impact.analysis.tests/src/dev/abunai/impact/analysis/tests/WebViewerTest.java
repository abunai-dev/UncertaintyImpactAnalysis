package dev.abunai.impact.analysis.tests;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import dev.abunai.impact.analysis.webview.WebViewer;

public class WebViewerTest extends TestBase {

	@Test
	public void executeWebViewerAnalysis() throws IOException {
		new WebViewer(analysis).handle();
	}

	@Override
	protected String getFolderName() {
		return "BranchingOnlineShop";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

}

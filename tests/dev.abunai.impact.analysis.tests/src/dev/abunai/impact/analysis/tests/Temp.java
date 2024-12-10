package dev.abunai.impact.analysis.tests;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import dev.abunai.impact.analysis.webview.Transformer;

public class Temp extends TestBase {

	@Test
	public void executeInteractiveAnalysis() throws IOException {
		new Transformer(analysis).handle();
	}

	@Override
	protected String getFolderName() {
		return "BranchingOnlineShop";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}
	/*
	@Override
	protected String getBaseFolder() {
		return "casestudies/CaseStudy-CoronaWarnApp";
	}
	
	@Override
	protected String getFolderName() {
		return "CoronaWarnApp";
	}
	//*/

}

package dev.abunai.impact.analysis.tests;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import dev.abunai.impact.analysis.interactive.InteractiveAnalysisHandler;

public class InteractiveTest extends TestBase {

	@Test
	public void doStuff() throws IOException {
		new InteractiveAnalysisHandler(analysis).handle();
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

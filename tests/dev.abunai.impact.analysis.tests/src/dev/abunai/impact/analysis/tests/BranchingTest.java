package dev.abunai.impact.analysis.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BranchingTest extends TestBase {

	@Override
	protected String getFolderName() {
		return "BranchingOnlineShop";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Test
	public void testEntryLevelSystemCallBeforeBranch() {
		analysis.addBehaviorUncertaintyInEntryLevelSystemCall("_hq6RAITkEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(4, 3, 2);
	}

	@Test
	public void testBranch() {
		analysis.addBehaviorUncertaintyInBranch("_tmNjQNv1EeyYrpd_1AtxSw");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(2, 2, 2);
	}

	@Test
	public void testExternalCallInBranch() {
		analysis.addBehaviorUncertaintyInExternalCallAction("_QqAdMNv2EeyYrpd_1AtxSw");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(2, 1, 1);
	}

}

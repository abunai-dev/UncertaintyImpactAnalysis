package dev.abunai.impact.analysis.tests;

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
		analysis.getUncertaintySources().addBehaviorUncertaintyInEntryLevelSystemCall("_hq6RAITkEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(4, 3, 2);
	}

	@Test
	public void testBranch() {
		analysis.getUncertaintySources().addBehaviorUncertaintyInBranch("_tmNjQNv1EeyYrpd_1AtxSw");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(2, 2, 2);
	}

	@Test
	public void testExternalCallInBranch() {
		analysis.getUncertaintySources().addBehaviorUncertaintyInExternalCallAction("_QqAdMNv2EeyYrpd_1AtxSw");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(2, 2, 1);
	}

}

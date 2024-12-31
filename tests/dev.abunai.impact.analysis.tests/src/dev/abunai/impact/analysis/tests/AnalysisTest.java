package dev.abunai.impact.analysis.tests;

import org.junit.jupiter.api.Test;

public class AnalysisTest extends TestBase {

	@Override
	protected String getFolderName() {
		return "InternationalOnlineShop";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Test
	public void testComponentUncertainty() {
		analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext("_4YTEYITjEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(2, 2, 1);
	}

	@Test
	public void testBehaviorUncertainty() {
		analysis.getUncertaintySources().addBehaviorUncertaintyInSetVariableAction("_tMGKUITmEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(1, 1, 1);
	}

	@Test
	public void testResourceContainerActorUncertainty() {
		analysis.getUncertaintySources().addActorUncertaintyInResourceContainer("_qvz80ITgEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(11, 11, 1);
	}

	@Test
	public void testUsageScenarioActorUncertainty() {
		analysis.getUncertaintySources().addActorUncertaintyInUsageScenario("_LPnI8CHdEd6lJo4DCALHMw");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(4, 4, 1);
	}

	@Test
	public void testInterfaceUncertaintyInTheCenter() {
		analysis.getUncertaintySources().addInterfaceUncertaintyInInterface("_XGAZwITiEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, true);
		assertAnalysisResults(9, 9, 1);
	}

	@Test
	public void testInterfaceUncertaintyAtTheEdge() {
		analysis.getUncertaintySources().addInterfaceUncertaintyInInterface("_UhSk0ITjEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(6, 6, 1);
	}

	@Test
	public void testInterfaceUncertaintyInAllSignatures() {
		analysis.getUncertaintySources().addInterfaceUncertaintyInSignature("_W8bxkITjEeywmO_IpTxeAg");
		analysis.getUncertaintySources().addInterfaceUncertaintyInSignature("_YWtP0ITmEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(6, 6, 1);
	}

	@Test
	public void testInterfaceUncertaintyInASubsetOfSignatures() {
		analysis.getUncertaintySources().addInterfaceUncertaintyInSignature("_YWtP0ITmEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(3, 3, 1);
	}

	@Test
	public void testConnectorUncertaintyInTheCenter() {
		analysis.getUncertaintySources().addConnectorUncertaintyInConnector("_BYWIkITkEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(9, 9, 1);
	}

	@Test
	public void testConnectorUncertaintyAtTheEdge() {
		analysis.getUncertaintySources().addConnectorUncertaintyInConnector("_E9MIUITkEeywmO_IpTxeAg");
		analysis.propagate().printResults(true, true, true, false);
		assertAnalysisResults(6, 6, 1);
	}
}
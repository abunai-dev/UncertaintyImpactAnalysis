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
		analysis.addComponentUncertaintyInAssemblyContext("_4YTEYITjEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(2, 2, 1);
	}

	@Test
	public void testBehaviorUncertainty() {
		analysis.addBehaviorUncertaintyInSetVariableAction("_tMGKUITmEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(1, 1, 1);
	}

	@Test
	public void testResourceContainerActorUncertainty() {
		analysis.addActorUncertaintyInResourceContainer("_qvz80ITgEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(9, 6, 1);
	}

	@Test
	public void testUsageScenarioActorUncertainty() {
		analysis.addActorUncertaintyInUsageScenario("_LPnI8CHdEd6lJo4DCALHMw");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(4, 4, 1);
	}

	@Test
	public void testInterfaceUncertaintyInTheCenter() {
		analysis.addInterfaceUncertaintyInInterface("_XGAZwITiEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(9, 6, 1);
	}

	@Test
	public void testInterfaceUncertaintyAtTheEdge() {
		analysis.addInterfaceUncertaintyInInterface("_UhSk0ITjEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(6, 6, 1);
	}

	@Test
	public void testInterfaceUncertaintyInAllSignatures() {
		analysis.addInterfaceUncertaintyInSignature("_W8bxkITjEeywmO_IpTxeAg");
		analysis.addInterfaceUncertaintyInSignature("_YWtP0ITmEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(6, 6, 1);
	}

	@Test
	public void testInterfaceUncertaintyInASubsetOfSignatures() {
		analysis.addInterfaceUncertaintyInSignature("_YWtP0ITmEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(3, 3, 1);
	}

	@Test
	public void testConnectorUncertaintyInTheCenter() {
		analysis.addConnectorUncertaintyInConnector("_BYWIkITkEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(9, 6, 1);
	}

	@Test
	public void testConnectorUncertaintyAtTheEdge() {
		analysis.addConnectorUncertaintyInConnector("_E9MIUITkEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertAnalysisResults(6, 6, 1);
	}
}
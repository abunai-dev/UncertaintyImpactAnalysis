package dev.abunai.impact.analysis.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

		assertEquals(2, result.size());
	}

	@Test
	public void testBehaviorUncertainty() {
		analysis.addBehaviorUncertaintyInSetVariableAction("_tMGKUITmEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(1, result.size());
	}

	@Test
	public void testResourceContainerActorUncertainty() {
		analysis.addActorUncertaintyInResourceContainer("_qvz80ITgEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(9, result.size());
	}

	@Test
	public void testUsageScenarioActorUncertainty() {
		analysis.addActorUncertaintyInUsageScenario("_LPnI8CHdEd6lJo4DCALHMw");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(4, result.size());
	}

	@Test
	public void testInterfaceUncertaintyInTheCenter() {
		analysis.addInterfaceUncertaintyInInterface("_XGAZwITiEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(9, result.size());
	}

	@Test
	public void testInterfaceUncertaintyAtTheEdge() {
		analysis.addInterfaceUncertaintyInInterface("_UhSk0ITjEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(6, result.size());
	}

	@Test
	public void testConnectorUncertaintyInTheCenter() {
		analysis.addConnectorUncertaintyInConnector("_BYWIkITkEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(9, result.size());
	}

	@Test
	public void testConnectorUncertaintyAtTheEdge() {
		analysis.addConnectorUncertaintyInConnector("_E9MIUITkEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(6, result.size());
	}
}
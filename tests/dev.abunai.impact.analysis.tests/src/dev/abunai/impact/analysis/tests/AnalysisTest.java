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
		analysis.addComponentUncertainty("_4YTEYITjEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(2, result.size());
	}

	@Test
	public void testBehaviorUncertainty() {
		analysis.addBehaviorUncertainty("_tMGKUITmEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(1, result.size());
	}

	@Test
	public void testActorUncertainty() {
		analysis.addActorUncertainty("_qvz80ITgEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(9, result.size());
	}

	@Test
	public void testInterfaceUncertaintyInTheCenter() {
		analysis.addInterfaceUncertainty("_XGAZwITiEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(9, result.size());
	}

	@Test
	public void testInterfaceUncertaintyAtTheEdge() {
		analysis.addInterfaceUncertainty("_UhSk0ITjEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(6, result.size());
	}

	@Test
	public void testConnectorUncertaintyInTheCenter() {
		analysis.addConnectorUncertainty("_BYWIkITkEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(9, result.size());
	}

	@Test
	public void testConnectorUncertaintyAtTheEdge() {
		analysis.addConnectorUncertainty("_E9MIUITkEeywmO_IpTxeAg");
		var result = analysis.propagate();
		printResults(result, true, true, true, false);

		assertEquals(6, result.size());
	}
}
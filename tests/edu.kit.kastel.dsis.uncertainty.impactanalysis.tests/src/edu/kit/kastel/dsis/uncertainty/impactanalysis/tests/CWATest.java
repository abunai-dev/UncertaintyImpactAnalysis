package edu.kit.kastel.dsis.uncertainty.impactanalysis.tests;

import org.junit.jupiter.api.Test;

public class CWATest extends TestBase {

	@Override
	protected String getFolderName() {
		return "CWA";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}
	
	@Test
	public void testDFDs() {
		var sequences = analysis.findAllSequences();
		var result = analysis.evaluateDataFlows(sequences);

		var elementCount = 0;
		
		for(var sequence : sequences) {
			elementCount += sequence.getElements().size();
			System.out.println(sequence);
			System.out.println("\n\n");
		}
		
		System.out.println(elementCount);

	}

}

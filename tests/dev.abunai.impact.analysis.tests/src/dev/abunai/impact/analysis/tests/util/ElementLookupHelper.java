package dev.abunai.impact.analysis.tests.util;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import dev.abunai.impact.analysis.tests.TestBase;

public class ElementLookupHelper extends TestBase {
	@Override
	protected String getFolderName() {
		return "InternationalOnlineShop";
	}

	@Override
	protected String getFilesName() {
		return "default";
	}

	@Test
	public void printAllRelevantElements() {
		var resourceProvider = analysis.getResourceProvider();

		var elements = resourceProvider.lookupElementWithCondition(it -> it.eClass().equals(RepositoryPackage.eINSTANCE.getBasicComponent()));
		System.out.println(elements);
		
		var element = resourceProvider.lookupElementWithId("_nGp9cITjEeywmO_IpTxeAg");
		System.out.println(element);

	}
}

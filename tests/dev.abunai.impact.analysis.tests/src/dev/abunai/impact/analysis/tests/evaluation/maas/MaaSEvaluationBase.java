package dev.abunai.impact.analysis.tests.evaluation.maas;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.dataflowanalysis.analysis.utils.ResourceUtils;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.abunai.evaluationscenario.maas.Activator;
import dev.abunai.impact.analysis.tests.evaluation.EvaluationBase;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class MaaSEvaluationBase extends EvaluationBase {
	private static final Logger logger = Logger.getLogger(MaaSEvaluationBase.class);

	@BeforeAll
	public void skipIfNotPresent() {
		this.setup();
		var usageModelPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".usagemodel")
				.toString();
		var usageModelURI = ResourceUtils.createRelativePluginURI(usageModelPath, this.getModelProjectName());
		var resourceSet = new ResourceSetImpl();
		var usageModelResource = resourceSet.getResource(usageModelURI, true);

		if (usageModelResource == null) {
			logger.warn("Could not load MaaS models! Assuming evaluation scenario project is not present. Skipping...");
		}
		assumeTrue(usageModelResource != null);
	}
	
	@Override
	protected String getModelProjectName() {
		return "dev.abunai.evaluationscenario.maas";
	}
	
	@Override
	protected Class<? extends Plugin> getActivator() {
		return Activator.class;
	}

	@Override
	protected String getFilesName() {
		return "MaaS";
	}
}

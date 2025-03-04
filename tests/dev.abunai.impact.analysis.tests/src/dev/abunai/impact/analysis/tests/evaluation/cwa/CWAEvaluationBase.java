package dev.abunai.impact.analysis.tests.evaluation.cwa;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Paths;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysisBuilder;
import org.apache.log4j.Logger;
import org.dataflowanalysis.analysis.utils.ResourceUtils;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.abunai.evaluationscenario.cwa.Activator;
import dev.abunai.impact.analysis.tests.evaluation.EvaluationBase;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class CWAEvaluationBase extends EvaluationBase {
	private static final Logger logger = Logger.getLogger(CWAEvaluationBase.class);
	
	@BeforeAll
	public void skipIfNotPresent() {
		this.setup();
		var usageModelPath = Paths.get(getBaseFolder(), getFolderName(), getFilesName() + ".usagemodel")
				.toString();
		var usageModelURI = ResourceUtils.createRelativePluginURI(usageModelPath, this.getModelProjectName());
		var resourceSet = new ResourceSetImpl();
		var usageModelResource = resourceSet.getResource(usageModelURI, true);

		if (usageModelResource == null) {
			logger.warn("Could not load CWA models! Assuming evaluation scenario project is not present. Skipping...");
		}
		assumeTrue(usageModelResource != null);
	}
	
	@Override
	protected String getModelProjectName() {
		return "dev.abunai.evaluationscenario.cwa";
	}
	
	@Override
	protected Class<? extends Plugin> getActivator() {
		return Activator.class;
	}

	@Override
	protected String getFilesName() {
		return "default";
	}
}

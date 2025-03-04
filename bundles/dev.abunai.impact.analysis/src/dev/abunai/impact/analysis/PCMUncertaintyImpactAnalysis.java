package dev.abunai.impact.analysis;

import java.util.Optional;

import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.eclipse.core.runtime.Plugin;
import org.dataflowanalysis.analysis.pcm.PCMDataFlowConfidentialityAnalysis;
import org.dataflowanalysis.analysis.pcm.resource.PCMResourceProvider;

import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.model.UncertaintySourceCollection;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents the top-level object of the uncertainty impact analysis on Palladio Component Models (PCM Models)
 */
public class PCMUncertaintyImpactAnalysis extends PCMDataFlowConfidentialityAnalysis {
    private UncertaintySourceCollection uncertaintySourceCollection = null;

	/**
	 * Create a new {@link PCMUncertaintyImpactAnalysis} with the given resource provider,
	 * model project name and activator class of the same project
	 * @param resourceProvider {@link org.dataflowanalysis.analysis.resource.ResourceProvider} providing the model data to the analysis
	 * @param modelProjectName Project name of the modelling project that contains the model files
	 * @param modelProjectActivator Activator in the modelling project used for eclipse plugin activation
	 */
	public PCMUncertaintyImpactAnalysis(PCMResourceProvider resourceProvider, String modelProjectName,
										Optional<Class<? extends Plugin>> modelProjectActivator) {
		super(resourceProvider, modelProjectName,
				modelProjectActivator);
	}

	@Override
	public void initializeAnalysis() {
		super.initializeAnalysis();
        PCMFlowGraphCollection flowGraphs = super.findFlowGraphs();
        PropagationHelper propagationHelper = new PropagationHelper(flowGraphs, this.resourceProvider);
		this.uncertaintySourceCollection = new UncertaintySourceCollection(flowGraphs, propagationHelper);
	}

	/**
	 * Returns the {@link UncertaintySourceCollection} defined by the modelling files
	 * @return Returns the {@link UncertaintySourceCollection} containing the {@link dev.abunai.impact.analysis.model.source.UncertaintySource} objects in the modelling files
	 */
	public UncertaintySourceCollection getUncertaintySources() {
		return this.uncertaintySourceCollection;
	}

	/**
	 * Returns the propagated {@link UncertaintyImpactCollection} that results from propagating the {@link dev.abunai.impact.analysis.model.source.UncertaintySource} elements
	 * @return Returns the propagated {@link UncertaintyImpactCollection} with the contained result of the impact analysis
	 */
	public UncertaintyImpactCollection propagate() {
		return this.getUncertaintySources().propagate();
	}
}

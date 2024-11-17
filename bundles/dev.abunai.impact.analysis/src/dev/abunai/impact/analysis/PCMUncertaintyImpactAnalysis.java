package dev.abunai.impact.analysis;

import java.util.Optional;

import org.dataflowanalysis.analysis.pcm.core.PCMFlowGraphCollection;
import org.eclipse.core.runtime.Plugin;
import org.dataflowanalysis.analysis.pcm.PCMDataFlowConfidentialityAnalysis;
import org.dataflowanalysis.analysis.pcm.resource.PCMResourceProvider;

import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.model.UncertaintySourceCollection;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class PCMUncertaintyImpactAnalysis extends PCMDataFlowConfidentialityAnalysis {

	private PCMFlowGraphCollection flowGraphs = null;
	private PropagationHelper propagationHelper = null;
	private UncertaintySourceCollection uncertaintySourceCollection = null;

	public PCMUncertaintyImpactAnalysis(PCMResourceProvider resourceProvider, String modelProjectName,
										Optional<Class<? extends Plugin>> modelProjectActivator) {
		super(resourceProvider, modelProjectName,
				modelProjectActivator);
	}

	@Override
	public void initializeAnalysis() {
		super.initializeAnalysis();
		this.flowGraphs = super.findFlowGraphs();
		this.propagationHelper = new PropagationHelper(this.flowGraphs, this.resourceProvider);
		this.uncertaintySourceCollection = new UncertaintySourceCollection(this.flowGraphs, propagationHelper);
	}

	public UncertaintySourceCollection getUncertaintySources() {
		return this.uncertaintySourceCollection;
	}

	public UncertaintyImpactCollection propagate() {
		return this.getUncertaintySources().propagate();
	}
}

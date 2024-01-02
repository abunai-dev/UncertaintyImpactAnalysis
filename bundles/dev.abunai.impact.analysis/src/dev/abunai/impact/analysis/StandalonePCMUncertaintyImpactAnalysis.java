package dev.abunai.impact.analysis;

import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.Plugin;

import org.dataflowanalysis.analysis.StandalonePCMDataFlowConfidentialityAnalysis;
import org.dataflowanalysis.analysis.builder.AnalysisData;
import org.dataflowanalysis.analysis.core.ActionSequence;

import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.model.UncertaintySourceCollection;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class StandalonePCMUncertaintyImpactAnalysis extends StandalonePCMDataFlowConfidentialityAnalysis {

	private List<ActionSequence> actionSequences = null;
	private PropagationHelper propagationHelper = null;
	private UncertaintySourceCollection uncertaintySourceCollection = null;
	private AnalysisData analysisData;

	public StandalonePCMUncertaintyImpactAnalysis(AnalysisData analysisData, String modelProjectName, Optional<Class<? extends Plugin>> modelProjectActivator) {
		super(analysisData, modelProjectName, modelProjectActivator);
	}

	@Override
	public boolean initializeAnalysis() {
		if (super.initializeAnalysis()) {
			this.actionSequences = super.findAllSequences();
			this.propagationHelper = new PropagationHelper(this.actionSequences, analysisData.getResourceProvider());
			this.uncertaintySourceCollection = new UncertaintySourceCollection(this.actionSequences, propagationHelper);
			return true;
		} else {
			return false;
		}
	}

	public UncertaintySourceCollection getUncertaintySources() {
		return this.uncertaintySourceCollection;
	}

	public UncertaintyImpactCollection propagate() {
		return this.getUncertaintySources().propagate();
	}
}

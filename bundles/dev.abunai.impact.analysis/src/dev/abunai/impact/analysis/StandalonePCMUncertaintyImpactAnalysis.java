package dev.abunai.impact.analysis;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.AnalysisData;
import org.palladiosimulator.dataflow.confidentiality.analysis.core.AbstractStandalonePCMDataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;

import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.model.UncertaintySourceCollection;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class StandalonePCMUncertaintyImpactAnalysis extends AbstractStandalonePCMDataFlowConfidentialityAnalysis {

	private final AnalysisData analysisData;

	private List<ActionSequence> actionSequences = null;
	private PropagationHelper propagationHelper = null;
	private UncertaintySourceCollection uncertaintySourceCollection = null;

	public StandalonePCMUncertaintyImpactAnalysis(String modelProjectName, Class<? extends Plugin> pluginActivator,
			AnalysisData analysisData) {
		super(analysisData, Logger.getLogger(StandalonePCMUncertaintyImpactAnalysis.class), modelProjectName,
				pluginActivator);

		this.analysisData = analysisData;
	}

	@Override
	public boolean setupAnalysis() {
		return true;
	}

	@Override
	public boolean initializeAnalysis() {
		if (super.initializeAnalysis()) {
			this.actionSequences = super.findAllSequences();
			this.propagationHelper = new PropagationHelper(this.actionSequences, analysisData.getResourceLoader());
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

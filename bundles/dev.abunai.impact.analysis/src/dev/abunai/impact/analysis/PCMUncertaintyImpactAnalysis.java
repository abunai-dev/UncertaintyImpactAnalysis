package dev.abunai.impact.analysis;

import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.Plugin;
import org.dataflowanalysis.analysis.core.ActionSequence;
import org.dataflowanalysis.analysis.core.DataCharacteristicsCalculatorFactory;
import org.dataflowanalysis.analysis.core.NodeCharacteristicsCalculator;
import org.dataflowanalysis.analysis.pcm.PCMDataFlowConfidentialityAnalysis;
import org.dataflowanalysis.analysis.pcm.resource.PCMResourceProvider;

import dev.abunai.impact.analysis.model.UncertaintyImpactCollection;
import dev.abunai.impact.analysis.model.UncertaintySourceCollection;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class PCMUncertaintyImpactAnalysis extends PCMDataFlowConfidentialityAnalysis {

	private List<ActionSequence> actionSequences = null;
	private PropagationHelper propagationHelper = null;
	private UncertaintySourceCollection uncertaintySourceCollection = null;

	public PCMUncertaintyImpactAnalysis(NodeCharacteristicsCalculator nodeCharacteristicsCalculator,
			DataCharacteristicsCalculatorFactory dataCharacteristicsCalculatorFactory,
			PCMResourceProvider resourceProvider, String modelProjectName,
			Optional<Class<? extends Plugin>> modelProjectActivator) {
		super(nodeCharacteristicsCalculator, dataCharacteristicsCalculatorFactory, resourceProvider, modelProjectName,
				modelProjectActivator);
	}

	@Override
	public boolean initializeAnalysis() {
		if (super.initializeAnalysis()) {
			this.actionSequences = super.findAllSequences();
			this.propagationHelper = new PropagationHelper(this.actionSequences, this.resourceProvider);
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

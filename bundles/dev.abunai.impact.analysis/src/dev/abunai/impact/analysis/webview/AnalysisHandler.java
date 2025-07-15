package dev.abunai.impact.analysis.webview;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

/**
 * Handles the adding of uncertainties to the analysis
 */
class AnalysisHandler {
	private final PCMUncertaintyImpactAnalysis analysis;

	public AnalysisHandler(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * Adds uncertainties to the analysis and executes it
	 * @param selections The selections made in the viewer
	 */
	public void handle(List<SelectionData> selections) {
		for (SelectionData selection : selections) {
			Optional<EObject> component = analysis.getResourceProvider().lookupElementWithId(selection.component);
			if (component.isPresent()) {
				addToAnalysis(component.get(), selection.component);
			}
		}
		
		analysis.propagate().printResults(true, true, true, false);
	}
	
	private void addToAnalysis(EObject component, String id) {
		if (component instanceof AssemblyContext) {
			analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext(id);
		}
		if (component instanceof AssemblyConnector) {
			analysis.getUncertaintySources().addConnectorUncertaintyInConnector(id);
		}
		if (component instanceof ProvidedDelegationConnector) {
			analysis.getUncertaintySources().addConnectorUncertaintyInConnector(id);
		}
		if (component instanceof ResourceContainer) {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(id);
		}
		if (component instanceof UsageScenario) {
			analysis.getUncertaintySources().addActorUncertaintyInUsageScenario(id);
		}
		if (component instanceof Signature) {
			analysis.getUncertaintySources().addInterfaceUncertaintyInSignature(id);
		}
		if (component instanceof Interface) {
			analysis.getUncertaintySources().addInterfaceUncertaintyInInterface(id);
		}
		if (component instanceof ExternalCallAction) {
			analysis.getUncertaintySources().addBehaviorUncertaintyInExternalCallAction(id);
		}
		if (component instanceof EntryLevelSystemCall) {
			analysis.getUncertaintySources().addBehaviorUncertaintyInEntryLevelSystemCall(id);
		}
		if (component instanceof SetVariableAction) {
			analysis.getUncertaintySources().addBehaviorUncertaintyInSetVariableAction(id);
		}
		if (component instanceof Branch) {
			analysis.getUncertaintySources().addBehaviorUncertaintyInBranch(id);
		}
	}
}

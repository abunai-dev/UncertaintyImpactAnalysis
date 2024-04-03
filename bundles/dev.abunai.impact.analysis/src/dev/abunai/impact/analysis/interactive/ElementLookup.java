package dev.abunai.impact.analysis.interactive;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EObject;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;
import org.dataflowanalysis.analysis.pcm.utils.PCMQueryUtils;
import org.dataflowanalysis.pcm.extension.model.confidentiality.characteristics.EnumCharacteristic;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.AbstractAssignee;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.NodeCharacteristicsPackage;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.ResourceAssignee;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.UsageAssignee;
import org.eclipse.emf.ecore.EClass;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffPackage;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;

class ElementLookup {
	
	private final PCMUncertaintyImpactAnalysis analysis;

	public ElementLookup(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
	}
	
	public List<EObject> getElementsOfType(ArchitecturalElementType type) {
		switch(type) {
			case COMPONENT: return getAssemblyContexts();
			case CONNECTOR: return getExternalCallsAndEntryLevelSystemCalls();
			case INTERFACE: return getOperationSignatures();
			case BEHAVIOR_DESCRIPTION: return getSetVariableActions();
			case EXTERNAL_RESOURCE: return getNodeCharacteristicsAssignees();
			default: throw new IllegalArgumentException("Not a valid architectural element type");
		}
	}
	
	private List<EObject> findAllElementsOfType(EClass targetType) {
		List<EObject> result = new ArrayList<EObject>();

		while (true) {
			var element = analysis.getResourceProvider()
					.lookupElementWithCondition(it -> it.eClass().equals(targetType) && !result.contains(it));

			if (element.isPresent()) {
				result.add(element.get());
			} else {
				break;
			}
		}

		return result;
	}
	
	private List<EObject> getNodeCharacteristicsAssignees() {
		List<EObject> resourceAssignees = findAllElementsOfType(NodeCharacteristicsPackage.eINSTANCE.getResourceAssignee());
		List<EObject> usageAssignees = findAllElementsOfType(NodeCharacteristicsPackage.eINSTANCE.getUsageAssignee());
		
		resourceAssignees.addAll(usageAssignees);

		return resourceAssignees;
	}

	private List<EObject> getSetVariableActions() {
		return findAllElementsOfType(SeffPackage.eINSTANCE.getSetVariableAction());
	}

	private List<EObject> getOperationSignatures() {
		return findAllElementsOfType(RepositoryPackage.eINSTANCE.getOperationSignature());
	}

	private List<EObject> getExternalCallsAndEntryLevelSystemCalls() {
		List<EObject> externalCalls = findAllElementsOfType(SeffPackage.eINSTANCE.getExternalCallAction());
		List<EObject> entryLevelSystemCalls = findAllElementsOfType(UsagemodelPackage.eINSTANCE.getEntryLevelSystemCall());
		
		externalCalls.addAll(entryLevelSystemCalls);
		
		return externalCalls;

	}
	
	private List<EObject> getAssemblyContexts() {
		return findAllElementsOfType(CompositionPackage.eINSTANCE.getAssemblyContext());
	}
}

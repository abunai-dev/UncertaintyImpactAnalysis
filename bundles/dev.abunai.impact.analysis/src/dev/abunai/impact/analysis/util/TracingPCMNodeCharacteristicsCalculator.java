package dev.abunai.impact.analysis.util;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.dataflow.confidentiality.analysis.PCMAnalysisUtils;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.pcm.PCMQueryUtils;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.repository.OperationalDataStoreComponent;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.profile.ProfileConstants;
import org.palladiosimulator.mdsdprofiles.api.StereotypeAPI;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationPackage;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public class TracingPCMNodeCharacteristicsCalculator {

	private final String filterID;
	private final EObject node;

	public TracingPCMNodeCharacteristicsCalculator(Entity node, String filterID) {
		this.node = node;
		this.filterID = filterID;
	}

	public boolean isAnnotatedWithNodeCharacteristic(Deque<AssemblyContext> context) {
		// Note that this already applies the filtering using the filterID
		return !this.getNodeCharacteristics(context).isEmpty();
	}

	private List<EnumCharacteristic> evaluateNodeCharacteristics(EObject object) {
		List<EnumCharacteristic> nodeCharacteristics = new ArrayList<>();
		var enumCharacteristics = StereotypeAPI.<List<EnumCharacteristic>>getTaggedValueSafe(object,
				ProfileConstants.characterisable.getValue(), ProfileConstants.characterisable.getStereotype());
		if (enumCharacteristics.isPresent()) {
			var nodeEnumCharacteristics = enumCharacteristics.get();
			for (EnumCharacteristic nodeEnumCharacteristic : nodeEnumCharacteristics) {
				if (nodeEnumCharacteristic.getId().equals(this.filterID)) {
					nodeCharacteristics.add(nodeEnumCharacteristic);
				}
			}
		}
		return nodeCharacteristics;
	}
	
	// Simple copy from PCMNnodeCharacteriticsCalculator, adapted for EnumCharacteristics
	
	 /**
     * Returns the node characteristics that are present at the given node with the assembly context provided. For User Actions the assembly context should be empty
     * @param context SEFF assembly context provided to the method. Should be empty for User Sequence Elements
     * @return Returns a list of node characteristics that are present at the current node
     */
    public List<EnumCharacteristic> getNodeCharacteristics(Deque<AssemblyContext> context) {
    	if (this.node instanceof AbstractUserAction) {
    		return getUserNodeCharacteristics((AbstractUserAction) this.node);
    	} else if(this.node instanceof AbstractAction) {
    		return getSEFFNodeCharacteristics(context);
    	} else if (this.node instanceof OperationalDataStoreComponent) {
    		return getSEFFNodeCharacteristics(context);
    	}
    	throw new IllegalArgumentException("Cannot calculate node characteristics of unknown type");
    }
    
    /**
     * Returns the node characteristics present at a node in a usage scenario
     * @param node Node in the usage scenario
     * @return List of node variables present at the node
     */
    private List<EnumCharacteristic> getUserNodeCharacteristics(AbstractUserAction node) {
    	var usageScenario = PCMQueryUtils.findParentOfType(node, UsageScenario.class, false).get();
    	return this.evaluateNodeCharacteristics(usageScenario);
    }
    
    /**
     * Returns the node characteristics present at a node in a SEFF context
     * @param context Context of the SEFF node
     * @return List of node characteristics present at the given node in the context provided
     */
    private List<EnumCharacteristic> getSEFFNodeCharacteristics(Deque<AssemblyContext> context) {
    	List<EnumCharacteristic> nodeVariables = new ArrayList<>();
    	
    	var allocations = PCMAnalysisUtils.lookupElementOfType(AllocationPackage.eINSTANCE.getAllocation()).stream()
    			.filter(Allocation.class::isInstance)
    			.map(Allocation.class::cast)
    			.collect(Collectors.toList());
    	
    	Optional<Allocation> allocation = allocations.stream()
    			.filter(it -> it.getAllocationContexts_Allocation().stream()
    					.map(alloc -> alloc.getAssemblyContext_AllocationContext())
    					.anyMatch(context.getFirst()::equals)
    			)
    			.findFirst();
    	
    	if (allocation.isEmpty()) {
    		throw new IllegalStateException();
    	}
    	
    	var allocationContexts = allocation.get().getAllocationContexts_Allocation();
    	    	
    	for (AllocationContext allocationContext : allocationContexts) {
    		if (context.contains(allocationContext.getAssemblyContext_AllocationContext())) {
        		nodeVariables.addAll(this.evaluateNodeCharacteristics(allocationContext.getResourceContainer_AllocationContext()));
    		}
    	}
    	return nodeVariables;
	}

}

package dev.abunai.impact.analysis.interactive;

import java.util.ArrayList;
import java.util.List;

import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.NodeCharacteristicsPackage;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.ResourceAssignee;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.UsageAssignee;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

class ExternalEntityLookup extends EntityLookup {

	private final List<ResourceAssignee> resourceAssignees;
	private final List<UsageAssignee> usageAssignees;
	
	public ExternalEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		resourceAssignees = findAllElementsOfType(NodeCharacteristicsPackage.eINSTANCE.getResourceAssignee(), ResourceAssignee.class);
		usageAssignees = findAllElementsOfType(NodeCharacteristicsPackage.eINSTANCE.getUsageAssignee(), UsageAssignee.class);
		
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> results = new ArrayList<>();
		results.addAll(resourceAssignees);
		results.addAll(usageAssignees);
		return results;
	}

	@Override
	public void addToAnalysis(int index) {
		if (index < resourceAssignees.size()) {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(resourceAssignees.get(index).getId());
		} else {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(usageAssignees.get(index - resourceAssignees.size()).getId());
		}
	}

}

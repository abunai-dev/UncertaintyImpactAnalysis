package dev.abunai.impact.analysis.interactive;

import java.util.ArrayList;
import java.util.List;

import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.NodeCharacteristicsPackage;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.ResourceAssignee;
import org.dataflowanalysis.pcm.extension.nodecharacteristics.nodecharacteristics.UsageAssignee;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

/**
 * Looks up elements relevant for the external {@link ArchitecturalElementType}
 */
public class ExternalEntityLookup extends EntityLookup {
	private final List<ResourceAssignee> resourceAssignees;
	private final List<UsageAssignee> usageAssignees;

	/**
	 * Create a new {@link ExternalEntityLookup} with the given impact analysis.
	 * It looks up the elements relevant for the external {@link ArchitecturalElementType}
	 * @param analysis PCM Impact analysis used to lookup elements
	 */
	public ExternalEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		this.resourceAssignees = this.findAllElementsOfType(NodeCharacteristicsPackage.eINSTANCE.getResourceAssignee(), ResourceAssignee.class);
		this.usageAssignees = this.findAllElementsOfType(NodeCharacteristicsPackage.eINSTANCE.getUsageAssignee(), UsageAssignee.class);
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> results = new ArrayList<>();
		results.addAll(this.resourceAssignees);
		results.addAll(this.usageAssignees);
		return results;
	}

	@Override
	public void addToAnalysis(int index) {
		if (index < this.resourceAssignees.size()) {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(this.resourceAssignees.get(index).getId());
		} else {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(this.usageAssignees.get(index - resourceAssignees.size()).getId());
		}
	}

}

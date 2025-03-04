package dev.abunai.impact.analysis.interactive;

import java.util.List;
import java.util.ArrayList;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

/**
 * Looks up elements relevant for the interface {@link ArchitecturalElementType}
 */
public class InterfaceEntityLookup extends EntityLookup {
	private final List<OperationSignature> operationSignatures;
	private final List<Interface> interfaces;

	/**
	 * Create a new {@link InterfaceEntityLookup} with the given impact analysis.
	 * It looks up the elements relevant for the interface {@link ArchitecturalElementType}
	 * @param analysis PCM Impact analysis used to lookup elements
	 */
	public InterfaceEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		this.operationSignatures = this.findAllElementsOfType(RepositoryPackage.eINSTANCE.getOperationSignature(), OperationSignature.class);
		this.interfaces = this.findAllElementsOfType(RepositoryPackage.eINSTANCE.getInterface(), Interface.class);
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> result = new ArrayList<>();
		result.addAll(this.operationSignatures);
		result.addAll(this.interfaces);
		return result;
	}

	@Override
	public void addToAnalysis(int index) {
		if (index < operationSignatures.size()) {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(this.operationSignatures.get(index).getId());
		} else {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(this.interfaces.get(index - operationSignatures.size()).getId());
		}
	}

}

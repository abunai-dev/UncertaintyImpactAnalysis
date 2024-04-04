package dev.abunai.impact.analysis.interactive;

import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

public class InterfaceEntityLookup extends EntityLookup {

	private List<OperationSignature> operationSignatures;
	
	public InterfaceEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		operationSignatures = findAllElementsOfType(RepositoryPackage.eINSTANCE.getOperationSignature(), OperationSignature.class);
	}

	@Override
	public List<Entity> getEntities() {
		return operationSignatures.stream().map(e -> (Entity)e).toList();
	}

	@Override
	public void addToAnalysis(int index) {
		analysis.getUncertaintySources().addInterfaceUncertaintyInSignature(operationSignatures.get(index).getId());
	}

}

package dev.abunai.impact.analysis.interactive;

import java.util.List;
import java.util.ArrayList;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

public class InterfaceEntityLookup extends EntityLookup {

	private final List<OperationSignature> operationSignatures;
	private final List<Interface> interfaces;
	
	public InterfaceEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		operationSignatures = findAllElementsOfType(RepositoryPackage.eINSTANCE.getOperationSignature(), OperationSignature.class);
		interfaces = findAllElementsOfType(RepositoryPackage.eINSTANCE.getInterface(), Interface.class);
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> result = new ArrayList<>();
		result.addAll(operationSignatures);
		result.addAll(interfaces);
		return result;
	}

	@Override
	public void addToAnalysis(int index) {
		if (index < operationSignatures.size()) {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(operationSignatures.get(index).getId());
		} else {
			analysis.getUncertaintySources().addActorUncertaintyInResourceContainer(interfaces.get(index - operationSignatures.size()).getId());
		}
	}

}

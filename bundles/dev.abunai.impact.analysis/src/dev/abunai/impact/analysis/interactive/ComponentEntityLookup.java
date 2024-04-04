package dev.abunai.impact.analysis.interactive;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

class ComponentEntityLookup extends EntityLookup {

	private final List<AssemblyContext> assemblyContexts;
	PCMUncertaintyImpactAnalysis analysis;
	
	public ComponentEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		
		assemblyContexts = findAllElementsOfType(CompositionPackage.eINSTANCE.getAssemblyContext(), AssemblyContext.class);
	}

	@Override
	public List<Entity> getEntities() {
		return assemblyContexts.stream().map(e -> (Entity)e).toList();
	}

	@Override
	public void addToAnalysis(int index) {
		analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext(assemblyContexts.get(index).getId());
	}

}

package dev.abunai.impact.analysis.interactive;

import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

/**
 * Looks up elements relevant for the component {@link ArchitecturalElementType}
 */
public class ComponentEntityLookup extends EntityLookup {
	private final List<AssemblyContext> assemblyContexts;

	/**
	 * Create a new {@link ComponentEntityLookup} with the given impact analysis.
	 * It looks up the elements relevant for the component {@link ArchitecturalElementType}
	 * @param analysis PCM Impact analysis used to lookup elements
	 */
	public ComponentEntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		super(analysis);
		assemblyContexts = this.findAllElementsOfType(CompositionPackage.eINSTANCE.getAssemblyContext(), AssemblyContext.class);
	}

	@Override
	public List<Entity> getEntities() {
		return assemblyContexts.stream().map(e -> (Entity)e).toList();
	}

	@Override
	public void addToAnalysis(int index) {
		this.analysis.getUncertaintySources().addComponentUncertaintyInAssemblyContext(assemblyContexts.get(index).getId());
	}

}

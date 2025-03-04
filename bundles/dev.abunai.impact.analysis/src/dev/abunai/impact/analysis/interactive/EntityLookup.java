package dev.abunai.impact.analysis.interactive;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EObject;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;
import org.eclipse.emf.ecore.EClass;
import org.palladiosimulator.pcm.core.entity.Entity;

abstract class EntityLookup {
	
	protected final PCMUncertaintyImpactAnalysis analysis;

	public EntityLookup(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
	}
	
	public abstract List<Entity> getEntities();
	
	public abstract void addToAnalysis(int index);
	
	protected <T extends Entity> List<T> findAllElementsOfType(EClass targetType, Class<T> targetClass) {
		ArrayList<EObject> result = new ArrayList<EObject>();

		while (true) {
			var element = analysis.getResourceProvider()
					.lookupElementWithCondition(it -> it.eClass().equals(targetType) && !result.contains(it));

			if (element.isPresent()) {
				result.add(element.get());
			} else {
				break;
			}
		}

		return result.stream().filter(targetClass::isInstance).map(targetClass::cast).toList();
	}
}

package dev.abunai.impact.analysis.model.source;

import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;

import dev.abunai.impact.analysis.model.impact.UncertaintyImpact;

/**
 * Represents an abstract {@link UncertaintySource} on a given {@link Entity} object
 * @param <T> Type parameter of the affected {@link Entity}
 */
public abstract class UncertaintySource<T extends Entity> {
	/**
	 * Returns the architectural {@link Entity} that is affected by the {@link UncertaintySource}
	 * @return Returns the affected architectural element
	 */
	public abstract T getArchitecturalElement();

	/**
	 * Propagates the {@link UncertaintySource} to a list of {@link UncertaintyImpact} elements.
	 * Each {@link UncertaintyImpact} contains the impact of the {@link UncertaintySource} on a {@link org.dataflowanalysis.analysis.pcm.core.PCMTransposeFlowGraph} element
	 * @return Return a list of {@link UncertaintyImpact} elements containing the full impact on the transpose flow graphs
	 */
	public abstract List<? extends UncertaintyImpact<? extends T>> propagate();

	/**
	 * Returns a textual representation of the {@link UncertaintySource} type
	 * @return Returns a textual representation of the {@link UncertaintySource} type
	 */
	public abstract String getUncertaintyType();

	@Override
	public String toString() {
		return String.format("%s Uncertainty annotated to %s \"%s\" (%s).", this.getUncertaintyType(),
				this.getArchitecturalElement().getClass().getSimpleName().replace("Impl", ""),
				this.getArchitecturalElement().getEntityName(), this.getArchitecturalElement().getId());
	}
}

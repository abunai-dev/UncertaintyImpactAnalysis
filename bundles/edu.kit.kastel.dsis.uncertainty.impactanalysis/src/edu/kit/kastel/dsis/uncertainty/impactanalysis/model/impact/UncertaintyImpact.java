package edu.kit.kastel.dsis.uncertainty.impactanalysis.model.impact;

import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.core.entity.Entity;

import edu.kit.kastel.dsis.uncertainty.impactanalysis.model.source.UncertaintySource;

public abstract class UncertaintyImpact<P extends Entity> {

	public abstract UncertaintySource<P> getOrigin();

	public abstract AbstractPCMActionSequenceElement<?> getAffectedElement();

	public abstract Optional<ActionSequence> getAffectedDataFlow();

	@Override
	public String toString() {
		var generalInfo = String.format("%s Uncertainty Impact on %s with ID %s (represeting a %s).",
				this.getOrigin().getUncertaintyType(), this.getAffectedElement().getClass().getSimpleName(),
				EcoreUtil.getID(this.getAffectedElement().getElement()), this.getAffectedElement().getElement().getClass().getSimpleName());
		var originInfo = String.format("Origin of this impact: %s", this.getOrigin().toString());
		var affectedDataFlowInfo = String.format("Affected Data Flow: %s", this.getAffectedDataFlow().get()
				.getElements().stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
		var emptyLine = "";

		return String.join(System.lineSeparator(), generalInfo, originInfo, affectedDataFlowInfo, emptyLine);
	}
}

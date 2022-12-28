package edu.kit.kastel.dsis.uncertainty.impactanalysis.util;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.StartAction;

public class PropagationHelper {
	
	private List<ActionSequence> actionSequences;
	
	public PropagationHelper(List<ActionSequence> actionSequences) {
		this.actionSequences = actionSequences;
	}
	
	public Optional<AssemblyContext> findAssemblyContext(String id) {
		return null;
	}
	
	public List<SEFFActionSequenceElement<StartAction>> findStartActionsOfAssemblyContext(AssemblyContext component) {
		return null;
	}
	
	public ActionSequence findActionSequenceWithElement(AbstractActionSequenceElement<?> element) {
		return null;
	}

}

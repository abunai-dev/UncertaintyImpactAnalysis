package dev.abunai.impact.analysis.model.source;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.repository.OperationSignature;

import dev.abunai.impact.analysis.model.impact.InterfaceUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class InterfaceUncertaintySource extends UncertaintySource<OperationSignature> {

	private final OperationSignature signature;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintySource(OperationSignature signature, PropagationHelper propagationHelper) {
		Objects.requireNonNull(signature);
		Objects.requireNonNull(propagationHelper);
		this.signature = signature;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public OperationSignature getArchitecturalElement() {
		return this.signature;
	}

	@Override
	public List<InterfaceUncertaintyImpact> propagate() {
		var startNodes = propagationHelper.findStartActionsOfSEFFsThatImplement(this.signature);
		var systemCallNodes = propagationHelper.findEntryLevelSystemCallsViaSignature(this.signature);
		var externalCallNodes = propagationHelper.findExternalCallsViaSignature(this.signature);

		List<? extends AbstractPCMActionSequenceElement<?>> allNodes = Stream
				.of(startNodes, systemCallNodes, externalCallNodes).flatMap(Collection::stream).toList();

		return allNodes.stream().map(it -> new InterfaceUncertaintyImpact(it, this, propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Interface";
	}

}

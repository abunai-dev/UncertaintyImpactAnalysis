package dev.abunai.impact.analysis.model.source;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.AbstractPCMActionSequenceElement;
import org.palladiosimulator.pcm.repository.OperationInterface;

import dev.abunai.impact.analysis.model.impact.InterfaceUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

public class InterfaceUncertaintySource extends UncertaintySource<OperationInterface> {

	private final OperationInterface interfaze;
	private final PropagationHelper propagationHelper;

	public InterfaceUncertaintySource(OperationInterface interfaze, PropagationHelper propagationHelper) {
		Objects.requireNonNull(interfaze);
		Objects.requireNonNull(propagationHelper);
		this.interfaze = interfaze;
		this.propagationHelper = propagationHelper;
	}

	@Override
	public OperationInterface getArchitecturalElement() {
		return this.interfaze;
	}

	@Override
	public List<InterfaceUncertaintyImpact> propagate() {
		var startNodes = propagationHelper.findStartActionsOfSEFFsThatImplement(this.interfaze);
		var systemCallNodes = propagationHelper.findEntryLevelSystemCallsViaInterface(this.interfaze);
		var externalCallNodes = propagationHelper.findExternalCallsViaInterface(this.interfaze);

		List<? extends AbstractPCMActionSequenceElement<?>> allNodes = Stream
				.of(startNodes, systemCallNodes, externalCallNodes).flatMap(Collection::stream).toList();

		return allNodes.stream().map(it -> new InterfaceUncertaintyImpact(it, this, propagationHelper)).toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Interface";
	}

}

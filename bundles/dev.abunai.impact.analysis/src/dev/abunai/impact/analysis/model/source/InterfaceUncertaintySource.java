package dev.abunai.impact.analysis.model.source;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.dataflowanalysis.analysis.pcm.core.AbstractPCMVertex;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.OperationSignature;

import dev.abunai.impact.analysis.model.impact.InterfaceUncertaintyImpact;
import dev.abunai.impact.analysis.util.PropagationHelper;

/**
 * Represents a source of uncertainty on an {@link OperationSignature}
 */
public class InterfaceUncertaintySource extends UncertaintySource<OperationSignature> {
	private final OperationSignature signature;
	private final PropagationHelper propagationHelper;

	/**
	 * Create a new {@link InterfaceUncertaintySource} with the given affected action element
	 * @param signature Affected {@link OperationSignature}
	 * @param propagationHelper Propagation helper used to determine affected transpose flow graph elements
	 */
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
		var startNodes = this.propagationHelper.findStartActionsOfSEFFsThatImplement(this.signature);
		var systemCallNodes = this.propagationHelper.findEntryLevelSystemCallsViaSignature(this.signature);
		var externalCallNodes = this.propagationHelper.findExternalCallsViaSignature(this.signature);

		List<? extends AbstractPCMVertex<?>> allNodes = Stream
				.of(startNodes, systemCallNodes, externalCallNodes)
				.flatMap(Collection::stream)
				.toList();

		return allNodes.stream()
				.map(it -> new InterfaceUncertaintyImpact(it, this, propagationHelper))
				.toList();
	}

	@Override
	public String getUncertaintyType() {
		return "Interface";
	}
}

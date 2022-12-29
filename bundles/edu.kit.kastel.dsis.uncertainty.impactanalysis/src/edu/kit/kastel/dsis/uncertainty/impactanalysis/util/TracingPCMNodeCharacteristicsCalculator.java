package edu.kit.kastel.dsis.uncertainty.impactanalysis.util;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.CharacteristicValue;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.pcm.PCMNodeCharacteristicsCalculator;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.characteristics.EnumCharacteristic;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.profile.ProfileConstants;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.Literal;
import org.palladiosimulator.mdsdprofiles.api.StereotypeAPI;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;

public class TracingPCMNodeCharacteristicsCalculator extends PCMNodeCharacteristicsCalculator {

	private String filterID;

	public TracingPCMNodeCharacteristicsCalculator(Entity node, String filterID) {
		super(node);
		this.filterID = filterID;
	}

	public boolean isAnnotatedWithNodeCharacteristic(Deque<AssemblyContext> context) {
		return !this.getNodeCharacteristics(Optional.ofNullable(context)).isEmpty();
	}

	public Optional<List<EnumCharacteristic>> getEnumCharactiertics(EObject object) {
		return StereotypeAPI.<List<EnumCharacteristic>>getTaggedValueSafe(object,
				ProfileConstants.characterisable.getValue(), ProfileConstants.characterisable.getStereotype());
	}

	@Override
	protected List<CharacteristicValue> evaluateNodeCharacteristics(EObject object) {
		List<CharacteristicValue> nodeCharacteristics = new ArrayList<>();
		var enumCharacteristics = StereotypeAPI.<List<EnumCharacteristic>>getTaggedValueSafe(object,
				ProfileConstants.characterisable.getValue(), ProfileConstants.characterisable.getStereotype());
		if (enumCharacteristics.isPresent()) {
			var nodeEnumCharacteristics = enumCharacteristics.get();
			for (EnumCharacteristic nodeEnumCharacteristic : nodeEnumCharacteristics) {
				if (nodeEnumCharacteristic.getId().equals(this.filterID)) {
					for (Literal nodeLiteral : nodeEnumCharacteristic.getValues()) {
						nodeCharacteristics.add(new CharacteristicValue(nodeEnumCharacteristic.getType(), nodeLiteral));
					}
				}
			}
		}
		return nodeCharacteristics;
	}

}

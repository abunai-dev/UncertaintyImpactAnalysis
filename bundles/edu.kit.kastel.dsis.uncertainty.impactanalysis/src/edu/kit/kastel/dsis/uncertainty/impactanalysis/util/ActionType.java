package edu.kit.kastel.dsis.uncertainty.impactanalysis.util;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

public enum ActionType {

	ENTRY_LEVEL_SYSTEM_CALL(EntryLevelSystemCall.class), 
	SET_VARIABLE_ACTION(SetVariableAction.class),
	EXTERNAL_CALL_ACTION(ExternalCallAction.class);

	public final Class<? extends Entity> clazz;

	private ActionType(Class<? extends Entity> clazz) {
		this.clazz = clazz;
	}
}

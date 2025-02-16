package dev.abunai.impact.analysis.webview;

import java.util.ArrayList;
import java.util.List;

import dev.abunai.impact.analysis.webview.jsonmodel.*;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;

class SystemTransformer implements AbstractTransformer<org.palladiosimulator.pcm.system.System> {
	
	@Override
	public JsonObject transform(org.palladiosimulator.pcm.system.System system) {
		List<JsonObject> contents = new ArrayList<>();
		
		for (var c : system.eContents()) {
			if (c instanceof AssemblyContext) {
				AssemblyContext a = (AssemblyContext)c;
				contents.add(new AssemblyContextJson(a.getEntityName(), a.getId()));
			} else if (c instanceof ProvidedDelegationConnector) {
				ProvidedDelegationConnector a = (ProvidedDelegationConnector)c;
				contents.add(new ProvidedDelegationConnectorJson(a.getId(),
						a.getAssemblyContext_ProvidedDelegationConnector().getId(),
						a.getOuterProvidedRole_ProvidedDelegationConnector().getId()));
			} else if (c instanceof AssemblyConnector) {
				AssemblyConnector  a = (AssemblyConnector )c;
				contents.add(new AssemblyConnectorJson(a.getId(), 
						a.getProvidedRole_AssemblyConnector().getEntityName(), 
						a.getRequiredRole_AssemblyConnector().getEntityName(),
						a.getProvidingAssemblyContext_AssemblyConnector().getId(), 
						a.getRequiringAssemblyContext_AssemblyConnector().getId()));
			} else if (c instanceof OperationProvidedRole) {
				OperationProvidedRole a = (OperationProvidedRole) c;
				contents.add(new OperationProvidedRoleJson(a.getEntityName(),
						a.getId(), 
						a.getProvidingEntity_ProvidedRole().getEntityName()));
			} else {
				System.out.println("Unknown system content: " + c.getClass());
			}
		}
		return new SystemJson(system.getId(), system.getEntityName(), contents);
	}
}

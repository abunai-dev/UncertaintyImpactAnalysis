package dev.abunai.impact.analysis.webview;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;

public class SystemTransformer implements AbstractTransformer<org.palladiosimulator.pcm.system.System> {
	
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

class SystemJson extends JsonObject {
	public String name;
	public List<JsonObject> contents;
	
	public SystemJson(String id, String name, List<JsonObject> contents) {
		super(id, "System");
		this.contents = contents;
	}
}

class AssemblyContextJson extends JsonObject {
	public String name;
	
	public AssemblyContextJson(String name, String id) {
		super(id, "AssemblyContext");
		this.name = name;
	}
}

class AssemblyConnectorJson extends JsonObject {
	public String providingRole;
	public String requiredRole;
	public String providingAssebly;
	public String requiredAssembly;
	public AssemblyConnectorJson(String id, String providingRole, String requiredRole, String providingAssebly,
			String requiredAssembly) {
		super(id, "AssemblyConnector");
		this.providingRole = providingRole;
		this.requiredRole = requiredRole;
		this.providingAssebly = providingAssebly;
		this.requiredAssembly = requiredAssembly;
	}	
}

class OperationProvidedRoleJson extends JsonObject {
	public String name;
	public String providingRole;
	public OperationProvidedRoleJson(String name, String id, String providingRole) {
		super(id, "OperationProvided");
		this.name = name;
		this.providingRole = providingRole;
	}
}

class ProvidedDelegationConnectorJson extends JsonObject {
	public String assemblyContext;
	public String outerProvidedRole;
	
	public ProvidedDelegationConnectorJson(String id, String assemblyContext, String outerProvidedRole) {
		super(id, "ProvidedDelegationConnector");
		this.assemblyContext = assemblyContext;
		this.outerProvidedRole = outerProvidedRole;
	}
}
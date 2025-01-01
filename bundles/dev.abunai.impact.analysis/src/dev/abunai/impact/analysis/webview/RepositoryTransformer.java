package dev.abunai.impact.analysis.webview;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

public class RepositoryTransformer implements AbstractTransformer<Repository> {

	@Override
	public JsonObject transform(Repository repository) {
		SeffTransformer seffTransformer = new SeffTransformer();
		List<JsonObject> contents = new ArrayList<>();
		
		for (RepositoryComponent component : repository.getComponents__Repository()) {
			List<SeffJson> seffs = new ArrayList<>();
			List<ComponentInterfaceConnection> required = new ArrayList<>();
			List<ComponentInterfaceConnection> provided = new ArrayList<>();
			if (component instanceof BasicComponent) {
				BasicComponent basicComponent = (BasicComponent)component;
				for (var s : basicComponent.getServiceEffectSpecifications__BasicComponent()) {
					if (s instanceof ResourceDemandingSEFF) {
						ResourceDemandingSEFF seff = (ResourceDemandingSEFF)s;
						
						seffs.add(new SeffJson(seff.getId(), 
								seff.getBasicComponent_ServiceEffectSpecification().getEntityName() + "." + s.getDescribedService__SEFF().getEntityName(), 
								seff.getSteps_Behaviour().stream().map(a -> seffTransformer.transform(a)).toList()));
					}
				}
				for (var r : component.getRequiredRoles_InterfaceRequiringEntity()) {				
					if (r instanceof OperationRequiredRole) {
						OperationRequiredRole o = (OperationRequiredRole)r;
						required.add(new ComponentInterfaceConnection(o.getEntityName(), o.getRequiredInterface__OperationRequiredRole().getId()));
					}
				}
				for (var r : component.getProvidedRoles_InterfaceProvidingEntity()) {				
					if (r instanceof OperationProvidedRole) {
						OperationProvidedRole o = (OperationProvidedRole)r;
						required.add(new ComponentInterfaceConnection(o.getEntityName(), o.getProvidedInterface__OperationProvidedRole().getId()));
					}
				}
			}
			contents.add(new BasicComponentJson(component.getId(), component.getEntityName(), seffs, required, provided));
		}
		
		for (Interface i : repository.getInterfaces__Repository()) {
			List<String> signatures = new ArrayList<>();
			if (i instanceof OperationInterface) {
				OperationInterface operationInterface = (OperationInterface)i;
				for (OperationSignature o : operationInterface.getSignatures__OperationInterface()) {
					String returnType = getDataType(o.getReturnType__OperationSignature());
					String parameter = o.getParameters__OperationSignature().stream().map(p -> getDataType(p.getDataType__Parameter()) + " " + p.getParameterName()).collect(Collectors.joining(", "));
					
					signatures.add(
							returnType + " " +
							o.getEntityName() + "(" +
							parameter + ")"
					);
				}
			}
			contents.add(new InterfaceJson(i.getId(), i.getEntityName(), signatures));
		}
		
		for (DataType d : repository.getDataTypes__Repository()) {
			if (d instanceof CompositeDataType) {
				CompositeDataType compositeDataType = (CompositeDataType)d;
				List<String> contained = new ArrayList<String>();
				List<String> signatures = new ArrayList<>();
				for (InnerDeclaration inner : compositeDataType.getInnerDeclaration_CompositeDataType()) {
					signatures.add(inner.getEntityName() + ": " + getDataType(inner.getDatatype_InnerDeclaration()));
					if (inner.getDatatype_InnerDeclaration() instanceof CompositeDataType) {
						contained.add(((CompositeDataType)inner.getDatatype_InnerDeclaration()).getId());
					}
				}
				contents.add(new CompositeDataTypeJson(compositeDataType.getId(), compositeDataType.getEntityName(), signatures, contained));
			}
		}
		
		return new RepositoryJson(repository.getId(), contents);
	}
	
	String getDataType(DataType dataType) {
		if (dataType instanceof NamedElement) {
			NamedElement compositeDataType = (NamedElement)dataType;
			return compositeDataType.getEntityName();
		}
		if (dataType instanceof PrimitiveDataType) {
			PrimitiveDataType primitiveDataType = (PrimitiveDataType)dataType;
			return primitiveDataType.getType().getLiteral();
		}
		return "void";
	}

}

class RepositoryJson extends JsonObject {
	public List<JsonObject> contents;
	
	
	public RepositoryJson(String id, List<JsonObject> contents) {
		super(id, "Repository");
		this.contents = contents;
	}
	
}

class SeffJson extends JsonObject {
	public String signature;
	public List<ActionJson> actions;
	
	public SeffJson(String id, String signature, List<ActionJson> actions) {
		super(id, "Seff");
		this.signature = signature;
		this.actions = actions;
	}
	
}

class BasicComponentJson extends JsonObject {
	public String name;
	public List<SeffJson> seffs;
	public List<ComponentInterfaceConnection> required;
	public List<ComponentInterfaceConnection> provided;
	
	
	public BasicComponentJson(String id, String name, List<SeffJson> seffs, List<ComponentInterfaceConnection> required, List<ComponentInterfaceConnection> provided) {
		super(id, "BasicComponent");
		this.name = name;
		this.seffs = seffs;
		this.required = required;
		this.provided = provided;
	}
}

class ComponentInterfaceConnection {
	public String label;
	public String goalInterface;
	
	public ComponentInterfaceConnection(String label, String goalInterface) {
		this.label = label;
		this.goalInterface = goalInterface;
	}
}

class InterfaceJson extends JsonObject {
	public String name;
	public List<String> signatures;
	
	public InterfaceJson(String id, String name, List<String> signatures) {
		super(id, "Interface");
		this.name = name;
		this.signatures = signatures;
	}
}

class CompositeDataTypeJson extends JsonObject {
	public String name;
	public List<String> signatures;
	public List<String> contained;
	
	protected CompositeDataTypeJson(String id, String name, List<String> signatures, List<String> contained) {
		super(id, "CompositeDataType");
		this.name = name;
		this.signatures = signatures;
		this.contained = contained;
	}
}
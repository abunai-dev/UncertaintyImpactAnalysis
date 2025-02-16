package dev.abunai.impact.analysis.webview;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

import java.util.List;
import java.util.Map;

import dev.abunai.impact.analysis.webview.jsonmodel.JsonObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EClass;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.palladiosimulator.pcm.allocation.AllocationPackage;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentPackage;
import org.palladiosimulator.pcm.system.SystemPackage;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;

/**
 * Transforms the sources of the analysis object into web usable data and starts the web viewer with them.
 */
public class WebViewer {
	private final PCMUncertaintyImpactAnalysis analysis;

	public WebViewer(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * Executes the transformation and starts the server
	 */
	public void handle() throws IOException {
		Map<String, byte[]> modelData = Map.of("system", exportTopLevelElement(SystemPackage.Literals.SYSTEM, new SystemTransformer()),
		"allocation", exportTopLevelElement(AllocationPackage.Literals.ALLOCATION, new AllocationTransformer()),
		"resourceEnvironment", exportTopLevelElement(ResourceenvironmentPackage.Literals.RESOURCE_ENVIRONMENT, new ResourceEnvironmentTransformer()),
		"usageModel", exportTopLevelElement(UsagemodelPackage.Literals.USAGE_MODEL, new UsageModelTransformer()),
		"repository", exportTopLevelElement(RepositoryPackage.Literals.REPOSITORY, new RepositoryTransformer()));
		
		Server server = new Server(modelData, new AnalysisHandler(analysis));
		server.start();
		System.out.println("Webview started at: http://localhost:" + Server.PORT + "/");
		System.out.println("Press enter to stop server");
		System.in.read();
	}
	
	private <T extends EObject> byte[] exportTopLevelElement(EClass elementClass, AbstractTransformer<T> transformer) throws IOException {
		List<T> elements = (List<T>) analysis.getResourceProvider().lookupToplevelElement(elementClass);
		List<JsonObject> jsonData = elements.stream().map(e -> transformer.transform(e)).toList();
	
		return new ObjectMapper().writeValueAsBytes(jsonData);
	}
}

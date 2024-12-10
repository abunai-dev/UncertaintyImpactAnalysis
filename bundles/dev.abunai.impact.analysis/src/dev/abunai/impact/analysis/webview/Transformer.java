package dev.abunai.impact.analysis.webview;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EClass;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import org.palladiosimulator.pcm.system.SystemPackage;

public class Transformer {
	private final PCMUncertaintyImpactAnalysis analysis;

	public Transformer(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
	}
	
	public void handle() throws IOException {
		exportTopLevelElement(SystemPackage.Literals.SYSTEM, new SystemTransformer(), "", "system");
	}
	
	private <T extends EObject> void exportTopLevelElement(EClass elementClass, AbstractTransformer<T> transformer, String path, String fileName) throws IOException {
		List<T> elements = (List<T>) analysis.getResourceProvider().lookupToplevelElement(elementClass);
		List<JsonObject> jsonData = elements.stream().map(e -> transformer.transform(e)).toList();
		
		if (!path.isEmpty() && !path.endsWith(File.separator)) {
			path += File.separator;
		}
		File file = new File(path + fileName + ".json");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		new ObjectMapper().writeValue(file, jsonData);
		
	}
}

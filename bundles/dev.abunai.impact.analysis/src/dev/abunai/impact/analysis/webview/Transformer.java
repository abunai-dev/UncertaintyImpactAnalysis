package dev.abunai.impact.analysis.webview;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.EClass;
import org.palladiosimulator.pcm.allocation.util.AllocationResourceImpl;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.entity.Entity;

import java.io.File;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;

public class Transformer {
	private final PCMUncertaintyImpactAnalysis analysis;

	public Transformer(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
	}
	
	public void handle() throws IOException {
		//var a = findAllElementsOfType(CompositionPackage.eINSTANCE.getAssemblyContext(), AssemblyContext.class);
		//for(var b : a) {printAssembly(b);}
		var resources = analysis.getResourceProvider().getResources();
		int i = 0;
		for (Resource r : resources) {
			MySerielizableObject m = new MySerielizableObject(r.getContents().get(0));
			File file = new File(i++ + ".json");
			if (!file.exists()) file.createNewFile();
			new ObjectMapper().writeValue(file, m);
		}
		//System.out.println(analysis.getResourceProvider().lookupToplevelElement(null))
		
	}
	
	void print(EObject e, int indent) {
		System.out.print(" ".repeat(indent*2));
		System.out.println(new MySerielizableObject(e));
		for (EObject c : e.eContents()) {
			print(c, indent+1);
		}
	}
	
}

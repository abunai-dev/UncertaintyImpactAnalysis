package dev.abunai.impact.analysis.interactive;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.palladiosimulator.pcm.core.entity.Entity;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import dev.abunai.impact.analysis.PCMUncertaintyImpactAnalysis;

import com.fasterxml.jackson.core.type.TypeReference;

public class InteractiveAnalysisHandler {
	
	private final String JSON_URL = "https://arc3n.abunai.dev/data.json";
	
	private final PCMUncertaintyImpactAnalysis analysis;
	private final Scanner scanner;
	
	public InteractiveAnalysisHandler(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
		scanner = new Scanner(System.in);
	}
	
	public void handle() throws IOException {
		System.out.println("Enter an id to check for:");
		int id = getIntFromInput();
		List<JsonUncertainty> uncertainties = getAllUncertainties();
		JsonUncertainty uncertainty = uncertainties.stream().filter(u -> u.id() == id).findFirst().orElse(null);
		if (uncertainty == null) {
			String notFoundText = String.format("No uncertainty with id %i found.", id);
			throw new IllegalArgumentException(notFoundText);
		}
		ArchitecturalElementType type = ArchitecturalElementType.getFromName(uncertainty.classes().architecturalElementType());
		EntityLookup entityLookup = generateEntityLookUp(type);
		selectElement(entityLookup);
		scanner.close();
	}
	
	private int getIntFromInput() {
		int input = -1;
		try {
			input = scanner.nextInt();
		} catch (InputMismatchException e) {
			scanner.close();
			throw new IllegalArgumentException(String.format("%s is not a valid number.", input));
		}
		return input;
	}
	
	
	private List<JsonUncertainty> getAllUncertainties() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		URL url = new URL(JSON_URL);
		JsonNode node = mapper.readTree(url);
		ObjectReader reader = mapper.readerFor(new TypeReference<List<JsonUncertainty>>() {});
		return reader.readValue(node);
	}
	
	private void selectElement(EntityLookup entityLookup) {
		System.out.println("Select one of these element.");
		List<Entity> allEntities = entityLookup.getEntities();
		for (int i = 0; i < allEntities.size(); i++) {
			Entity element = allEntities.get(i);
			System.out.println(String.format("%d) %s (%s)", i + 1, element.getEntityName(), element.getId()));
		}
		System.out.println("Enter line number:");
		int index = getIntFromInput();
		if (index < 1 || index > allEntities.size()) {
			throw new IllegalArgumentException(String.format("Number %d is out of range.", index));
		}
		entityLookup.addToAnalysis(index);
	}
	
	private EntityLookup generateEntityLookUp(ArchitecturalElementType type) {
		switch(type) {
			case COMPONENT: return new ComponentEntityLookup(analysis);
			case CONNECTOR: return new ConnectorEntityLookup(analysis);
			case INTERFACE: return new InterfaceEntityLookup(analysis);
			case BEHAVIOR_DESCRIPTION: return new BehaviorEntityLookup(analysis);
			case EXTERNAL_RESOURCE: return new ExternalEntityLookup(analysis);
		}
		return null;
	}
}

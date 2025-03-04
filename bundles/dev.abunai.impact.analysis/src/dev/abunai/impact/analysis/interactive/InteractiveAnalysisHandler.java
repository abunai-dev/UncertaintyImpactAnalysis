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

/**
 * Handles interactive input for the analysis
 */
public class InteractiveAnalysisHandler {
	private static final String JSON_URL = "https://arc3n.abunai.dev/data.json";
	
	private final PCMUncertaintyImpactAnalysis analysis;
	private final Scanner scanner;

	/**
	 * Creates a new handler for interactive input for the given analysis
	 * @param analysis Given analysis to which the interactive input applies
	 */
	public InteractiveAnalysisHandler(PCMUncertaintyImpactAnalysis analysis) {
		this.analysis = analysis;
		scanner = new Scanner(System.in);
	}

	/**
	 * Handles an interaction via the command line with the analysis
	 * @throws IOException Thrown when uncertainties cannot be loaded from json
	 */
	public void handle() throws IOException {
		System.out.println("Enter an id of an uncertainty to check for:");
		int id = getIntFromInput();
		List<JsonUncertainty> uncertainties = getAllUncertainties();
		JsonUncertainty uncertainty = uncertainties.stream().filter(u -> u.id() == id).findFirst().orElse(null);
		if (uncertainty == null) {
			String notFoundText = String.format("No uncertainty with id %d found.", id);
			throw new IllegalArgumentException(notFoundText);
		}
		
		ArchitecturalElementType type = ArchitecturalElementType.getFromName(uncertainty.classes().architecturalElementType());
		EntityLookup entityLookup = generateEntityLookUp(type);
		System.out.println();
		if(!selectElement(entityLookup)) {
			scanner.close();
			return;
		}
		scanner.close();
		System.out.println();
		
		
		analysis.propagate().printResults(true, true, true, false);
		
	}

	/**
	 * Gets an Integer from input allowing for a leading "#"
	 * @return Returns a Integer from system input
	 * @throws IllegalArgumentException Thrown when the given input is not an integer
	 */
	private int getIntFromInput() {
		String input = "";
		try {
			input = scanner.nextLine();
			if (input.startsWith("#")) {
				input = input.substring(1);
			}
			return Integer.parseInt(input);
		} catch (InputMismatchException | NumberFormatException e) {
			scanner.close();
			throw new IllegalArgumentException(String.format("%s is not a valid number.", input));
		}
	}


	/**
	 * Receives all uncertainties from arc3n
	 * @return Returns a list of {@link JsonUncertainty} retrieved from arc3n
	 * @throws IOException thrown when the {@link JsonUncertainty} objects cannot be retrieved
	 */
	private List<JsonUncertainty> getAllUncertainties() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		URL url = new URL(JSON_URL);
		JsonNode node = mapper.readTree(url);
		ObjectReader reader = mapper.readerFor(new TypeReference<List<JsonUncertainty>>() {});
		return reader.readValue(node);
	}

	/**
	 * Selects an element from the given {@link EntityLookup} object
	 * @param entityLookup {@link EntityLookup} that is picked from
	 * @return Returns true, if an uncertainty could be added.
	 * Otherwise, the method returns false
	 */
	private boolean selectElement(EntityLookup entityLookup) {
		List<Entity> allEntities = entityLookup.getEntities();
		if (allEntities.isEmpty()) {
			System.out.println("No uncertainties with that type found.");
			return false;
		}
		System.out.println("Select one of these element.");
		for (int i = 0; i < allEntities.size(); i++) {
			Entity element = allEntities.get(i);
			System.out.printf("%d) %s (%s)%n", i + 1, element.getEntityName(), element.getId());
		}
		System.out.println("Enter line number:");
		int index = getIntFromInput();
		if (index < 1 || index > allEntities.size()) {
			throw new IllegalArgumentException(String.format("Number %d is out of range.", index));
		}
		try {
			entityLookup.addToAnalysis(index - 1);
		} catch (IllegalArgumentException e) {
			System.out.println();
			System.out.println("The selected entity is not part of any dataflow.");
			System.out.println("No uncertainty with this entity found.");
			return false;
		}
		return true;
	}

	/**
	 * Generates an {@link EntityLookup} for the given {@link ArchitecturalElementType}
	 * @param type {@link ArchitecturalElementType} for which an {@link EntityLookup} should be created
	 * @return Returns an {@link EntityLookup} for the correct {@link ArchitecturalElementType}
	 */
	private EntityLookup generateEntityLookUp(ArchitecturalElementType type) {
        return switch (type) {
            case COMPONENT -> new ComponentEntityLookup(analysis);
            case CONNECTOR -> new ConnectorEntityLookup(analysis);
            case INTERFACE -> new InterfaceEntityLookup(analysis);
            case BEHAVIOR_DESCRIPTION -> new BehaviorEntityLookup(analysis);
            case EXTERNAL_RESOURCE -> new ExternalEntityLookup(analysis);
        };
    }
}

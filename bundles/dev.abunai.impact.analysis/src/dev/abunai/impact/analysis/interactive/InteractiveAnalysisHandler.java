package dev.abunai.impact.analysis.interactive;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.core.type.TypeReference;

public class InteractiveAnalysisHandler {
	
	private static final String JSON_URL = "https://arc3n.abunai.dev/data.json";
	
	public void handle() throws IOException {
		System.out.println("Enter an id to check for:");
		int id = getIntFromInput();
		List<JsonUncertainty> uncertainties = getAllUncertainties();
		JsonUncertainty uncertainty = uncertainties.stream().filter(u -> u.id() == id).findFirst().orElse(null);
		if (uncertainty == null) {
			String notFoundText = String.format("No uncertainty with id %i found.", id);
			throw new IllegalArgumentException(notFoundText);
		}
		List<Object> allElements = getAllElements(ArchitecturalElementType.getFromName(uncertainty.architecturalElementType()));
		Object elementToAnalize = selectElement(allElements);
	}
	
	private int getIntFromInput() {
		int input = -1;
		try (Scanner scanner = new Scanner(System.in)) {
			input = scanner.nextInt();
		} catch (InputMismatchException e) {
			throw new IllegalArgumentException(String.format("%s is not a valid number.", input));
		}
		return input;
	}
	
	private List<Object> getAllElements(ArchitecturalElementType element) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	private List<JsonUncertainty> getAllUncertainties() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		URL url = new URL(JSON_URL);
		JsonNode node = mapper.readTree(url);
		ObjectReader reader = mapper.readerFor(new TypeReference<List<JsonUncertainty>>() {});
		return reader.readValue(node);
	}
	
	private Object selectElement(List<Object> allElements) {
		System.out.println("Select one of these element, by giving its line number:");
		for (int i = 0; i < allElements.size(); i++) {
			System.out.println(String.format("%d) %s", i + 1, allElements.get(i)));
		}
		int index = getIntFromInput();
		if (index < 1 || index > allElements.size()) {
			throw new IllegalArgumentException(String.format("Number %d is out of range.", index));
		}
		return allElements.get(index);
	}
	
}

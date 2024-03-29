package dev.abunai.impact.analysis.interactive;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class InteractiveAnalysisHandler {
	
	public void handle() {
		int id = getID();
		System.out.println("Input was: " + id);
	}
	
	private int getID() {
		System.out.println("Please enter an id to check for:");
		int input = -1;
		while (input < 0) {
			try (Scanner scanner = new Scanner(System.in)) {
				input = scanner.nextInt();
			} catch (NoSuchElementException e) {
				System.out.println("Please enter a valid id:");
			}
		}
		
		return input;
	}
	
}

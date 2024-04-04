package dev.abunai.impact.analysis.interactive;

record Resource(String id, String name) {

	@Override
	public String toString() {
		return String.format("%s (%s)", name, id);
	}
}

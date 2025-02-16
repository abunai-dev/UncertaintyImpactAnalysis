package dev.abunai.impact.analysis.webview;

import java.util.UUID;

class Util {
	public static String generateUUID() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 7);
	}
}

package dev.abunai.impact.analysis.webview;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Manages the hosting of the web viewer
 */
class Server implements HttpHandler {

	private HttpServer server;
	public static final int PORT = 8080;
	private static final String BASE_FOLDER = Path.of("..", "..", "bundles", "dev.abunai.impact.analysis", "src", "dev", "abunai", "impact", "analysis", "webview", "viewer", "dist").toString();
	private Map<String, byte[]> getMap;
	private AnalysisHandler analysisHandler;

	/**
	 * Creates a Server object
	 * @param getMap Mapping of paths to data for GET requests
	 * @param analysisHandler Handler for POST requests to add uncertainties
	 */
	public Server(Map<String, byte[]> getMap, AnalysisHandler analysisHandler) {
		this.getMap = getMap;
		this.analysisHandler = analysisHandler;
	}

	/**
	 * Starts the web viewer on the default port
	 * @throws IOException
	 */
	public void start() throws IOException {
		if (server != null) {
            throw new IllegalStateException("Server already started");
        }

		System.setProperty("java.net.preferIPv4Stack", "true");

		server = HttpServer.create(new InetSocketAddress(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), PORT), 0);

        server.createContext("/", this);
        server.setExecutor(null);
        server.start();
	}

	/**
	 * Stops the web viewer
	 */
	public void stop() {
		server.stop(0);
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			handleGet(exchange);
		} else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			if (exchange.getRequestURI().getPath().equals("/analysis")) {
				try {
					analysisHandler.handle(geSelectiontBody(exchange));
				} catch (IOException e) {
					System.out.println(e);
					answerWithText("400: Could not handle request", 400, exchange);
					return;
				}
				answerWithText("200: Succes", 202, exchange);
			} else {
				answerWithText("404: Unknown endpoint", 404, exchange);
			}
		} else {
			answerWithText("404: Method not supported!", 404, exchange);
		}
	}
	
	private List<SelectionData> geSelectiontBody(HttpExchange exchange) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(exchange.getRequestBody());
		exchange.getRequestBody().close();
		ObjectReader reader = mapper.readerFor(new TypeReference<List<SelectionData>>(){});
		List<SelectionData> a = reader.readValue(json);
		System.out.println(a);
		return a;
	}

	private void handleGet(HttpExchange exchange) throws IOException {
		String requestedPath = exchange.getRequestURI().getPath();

		byte[] bytes = getMap.get(requestedPath.substring(1));
		if (bytes != null) {
			answerWithBytes(bytes, 200, exchange);
			return;
		}

		File originalFile = getDistFile(requestedPath);
        if (originalFile != null) {
			answerWithFile(originalFile, 200, exchange);
			return;
            
        } 
        
        File indexFile = getDistFile("/index.html");
		if (indexFile != null) {
			answerWithFile(indexFile, 200, exchange);
		} else {
			answerWithText("404: Not found!", 404, exchange);
		}
	}

	private void answerWithFile(File file, int code, HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().set("Content-Type", getMimeType(file));
		exchange.sendResponseHeaders(code, file.length());
        try (OutputStream os = exchange.getResponseBody();
             FileInputStream fis = new FileInputStream(file)) {
            fis.transferTo(os); // Transfer file content to the response
			os.flush();
        }
	}

	private String getMimeType(File file) {
		String name = file.getName();
		if (name.endsWith("js")) {
			return "application/javascript; charset=utf-8";
		}
		if (name.endsWith("css")) {
			return "text/css; charset=utf-8";
		}
		if (name.endsWith("html")) {
			return "text/html; charset=utf-";
		}
		return "text/plain; charset=utf-8";
	}

	private void answerWithBytes(byte[] bytes, int code, HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(code, bytes.length);
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(bytes);
		}
	}

	private void answerWithText(String answer, int code, HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(code, answer.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(answer.getBytes());
        }
	}

	private File getDistFile(String path) {
		File file = new File(BASE_FOLDER + path);
        if (file.exists() && !file.isDirectory()) {
			return file;
        }
        return null;
	}
}

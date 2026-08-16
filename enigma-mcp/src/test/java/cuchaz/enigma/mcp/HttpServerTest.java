package cuchaz.enigma.mcp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Tests that the HTTP MCP server can be connected to and can serve simple tools.
 */
public class HttpServerTest {
	private static final Process SERVER_PROCESS;
	private static final McpSyncClient CLIENT;

	static {
		try {
			SERVER_PROCESS = startServerProcess();
			CLIENT = connectClient(readPort(SERVER_PROCESS));
		} catch (IOException | RuntimeException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private static Process startServerProcess() throws IOException {
		String classpath = System.getProperty("java.class.path");

		ProcessBuilder builder = new ProcessBuilder(
				"java",
				"-cp", classpath,
				EnigmaMcpMain.class.getName(),
				"--jar", "build/test-inputs/test-inputs.jar",
				"--port", "0"
		);
		builder.redirectErrorStream(true);
		return builder.start();
	}

	private static int readPort(Process process) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
			String line;

			while ((line = reader.readLine()) != null) {
				int index = line.indexOf("http://localhost:");

				if (index >= 0) {
					String url = line.substring(index);
					String portText = url.substring("http://localhost:".length(), url.indexOf('/', "http://localhost:".length()));
					return Integer.parseInt(portText);
				}
			}
		}

		throw new IllegalStateException("MCP HTTP server did not report a listening port");
	}

	private static McpSyncClient connectClient(int port) {
		HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport.builder("http://localhost:" + port)
				.jsonMapper(McpJsonDefaults.getMapper())
				.build();

		McpSyncClient client = McpClient.sync(transport).build();
		client.initialize();
		return client;
	}

	@AfterClass
	public static void tearDown() {
		if (CLIENT != null) {
			CLIENT.close();
		}

		if (SERVER_PROCESS != null) {
			SERVER_PROCESS.destroy();
		}
	}

	@Test
	public void getEnigmaInfo() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"get_enigma_info",
				Map.of(),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("Enigma Version:"));
		assertTrue(text.contains("Jar path(s):"));
	}
}

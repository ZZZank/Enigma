package cuchaz.enigma.mcp;

import java.nio.file.FileSystem;
import java.util.Map;

import com.google.common.jimfs.Jimfs;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * @author ZZZank
 */
public abstract class Global {
	public static final McpSyncClient CLIENT;
	public static final FileSystem FILE_SYSTEM;

	static {
		String classpath = System.getProperty("java.class.path");

		// Starts MCP server with NO mapping, tests should add mapping themselves
		ServerParameters serverParams = ServerParameters.builder("java")
				.args("-cp", classpath, EnigmaMcpMain.class.getName(), "--jar", "build/test-inputs/test-inputs.jar")
				.build();

		StdioClientTransport transport = new StdioClientTransport(serverParams, McpJsonDefaults.getMapper());
		transport.setStdErrorHandler(msg -> System.out.println("[Enigma test server] " + msg));
		CLIENT = McpClient.sync(transport).build();
		CLIENT.initialize();

		FILE_SYSTEM = Jimfs.newFileSystem();
	}

	/// @see cuchaz.enigma.mcp.tool.ReloadMappingTool
	public static void clearMapping() {
		CLIENT.callTool(new McpSchema.CallToolRequest("reload_mapping", Map.of("clear_if_all_null", true), Map.of()));
	}

	public static <T> T requireInstanceOf(Class<T> type, Object o) {
		if (!type.isInstance(o)) {
			throw new IllegalArgumentException(String.format("Expected an instance of '%s', but got: %s", type, o));
		}

		return (T) o;
	}

	public static McpSchema.TextContent asTextContent(McpSchema.Content content) {
		return requireInstanceOf(McpSchema.TextContent.class, content);
	}

	public static void println(String text) {
		System.out.println(text);
	}
}

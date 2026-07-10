package cuchaz.enigma.mcp;

import java.nio.file.FileSystem;

import com.google.common.jimfs.Jimfs;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonDefaults;

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
		CLIENT = McpClient.sync(transport).build();
		CLIENT.initialize();

		FILE_SYSTEM = Jimfs.newFileSystem();
	}
}

package cuchaz.enigma.mcp.tool;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import cuchaz.enigma.EnigmaProject;
import cuchaz.enigma.mcp.EnigmaMcpMain;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;
import cuchaz.enigma.translation.mapping.serde.MappingParseException;

/**
 * @author ZZZank
 */
public record ReloadMappingTool(EnigmaProject project, EnigmaMcpMain main) implements TypedArgTool<ReloadMappingTool.ArgObject> {
	@Override
	public String name() {
		return "reload_mapping";
	}

	@Override
	public Class<ArgObject> argObjectType() {
		return ArgObject.class;
	}

	@Override
	public McpSchema.CallToolResult callTool(McpSyncServerExchange exchange,
			McpSchema.CallToolRequest request,
			ArgObject arg) {
		if (arg.clear_if_all_null && arg.path == null && arg.format == null) {
			try {
				main.loadMapping(null, null);
				return McpTools.ok("Mapping cleared.");
			} catch (IOException | MappingParseException e) {
				return McpTools.error("Error when clearing mapping: " + e);
			}
		}

		if (main.getMappingFormat() == null && arg.path == null && arg.format == null) {
			return McpTools.error("Got no arg, but there's no existed mapping param to reuse");
		}

		Path path = arg.path == null ? main.getMappingFile() : Paths.get(arg.path);
		MappingFormat format = arg.format == null ? main.getMappingFormat() : arg.format;

		try {
			main.loadMapping(format, path);
			return McpTools.ok("Mapping loaded.");
		} catch (IOException | MappingParseException e) {
			return McpTools.error("Error when loading mapping: " + e);
		}
	}

	@JsonClassDescription("Reload existed mapping or load new mapping")
	public static class ArgObject {
		@JsonProperty(defaultValue = "false")
		@JsonPropertyDescription("In true, omitting input for 'path' and 'format' will be interpreted as CLEARING existed mapping, instead of reloading the same mapping.")
		public boolean clear_if_all_null;
		@JsonPropertyDescription("Path to mapping file. Null input implies using current mapping file path.")
		public String path;
		@JsonPropertyDescription("Format of mapping file. Null implies using current mapping format.")
		public MappingFormat format;
	}
}

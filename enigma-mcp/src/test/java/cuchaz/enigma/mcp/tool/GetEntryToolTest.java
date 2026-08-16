package cuchaz.enigma.mcp.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link GetEntryTool}.
 */
public class GetEntryToolTest extends Global {

	@Test
	public void getClassEntry() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"get_entry",
				Map.of("entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		assertTrue(text.contains("class cuchaz/enigma/inputs/loneClass/LoneClass"));
		assertTrue(text.contains("Obfuscated name:"));
		assertTrue(text.contains("Access:"));
	}

	@Test
	public void getEntryNotFound() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"get_entry",
				Map.of("entry_description", "method nonexistent@cuchaz/enigma/inputs/loneClass/LoneClass"),
				Map.of()
		));

		assertTrue(result.isError());
	}

	@Test
	public void getEntryInvalidFormat() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"get_entry",
				Map.of("entry_description", "invalid!!!"),
				Map.of()
		));

		assertTrue(result.isError());
	}
}

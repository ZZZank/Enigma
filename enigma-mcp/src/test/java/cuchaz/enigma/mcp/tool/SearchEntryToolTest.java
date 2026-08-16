package cuchaz.enigma.mcp.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link SearchEntryTool}.
 *
 * <p>Uses only the {@code translation} package for class searches,
 * as it is confirmed to work reliably in the test JAR.
 */
public class SearchEntryToolTest extends Global {
	@Test
	public void searchClassWithLimit() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"search_entry",
				Map.of("entry_description", "class cuchaz/enigma/inputs/translation", "limit", 2),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("Found 2 matching"));
	}

	@Test
	public void searchClassNoMatch() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"search_entry",
				Map.of("entry_description", "class nonexistent/Class"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("No CLASS entries found"));
	}

	@Test
	public void searchEntryInvalidDescription() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"search_entry",
				Map.of("entry_description", "invalid_type foo"),
				Map.of()
		));

		assertTrue(result.isError());
	}
}

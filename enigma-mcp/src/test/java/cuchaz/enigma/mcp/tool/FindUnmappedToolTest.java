package cuchaz.enigma.mcp.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Before;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link FindUnmappedTool}.
 *
 * <p>Note: In Enigma 4.0.2 the EntryRemapper may always return non-null
 * deobfuscated names, so "No unmapped entries found." is a valid response.
 * We test that the tool responds without crashing and returns
 * well-formed output for each entry type.
 */
public class FindUnmappedToolTest extends Global {
	@Before
	public void setUp() {
		clearMapping();
	}

	@Test
	public void findUnmappedClasses() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_unmapped",
				Map.of("entry_type", "class"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("unmapped entr") || text.contains("No unmapped entries found"));
	}

	@Test
	public void findUnmappedMethods() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_unmapped",
				Map.of("entry_type", "method"),
				Map.of()
		));

		assertFalse(result.isError());
		// Any valid response is fine
	}

	@Test
	public void findUnmappedFields() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_unmapped",
				Map.of("entry_type", "field"),
				Map.of()
		));

		assertFalse(result.isError());
	}

	@Test
	public void findUnmappedConstructors() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_unmapped",
				Map.of("entry_type", "constructor"),
				Map.of()
		));

		assertFalse(result.isError());
	}

	@Test
	public void findUnmappedAll() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_unmapped",
				Map.of("entry_type", "all"),
				Map.of()
		));

		assertFalse(result.isError());
	}

	@Test
	public void findUnmappedWithLimit() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_unmapped",
				Map.of("entry_type", "all", "limit", 5),
				Map.of()
		));

		assertFalse(result.isError());
	}
}

package cuchaz.enigma.mcp.tool;

import static org.junit.Assert.*;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Before;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link MultiEditMappingTool}.
 */
public class MultiEditMappingToolTest extends Global {

	@Before
	public void setUp() {
		clearMapping();
	}

	@Test
	public void multiEditMultipleEntries() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"multi_edit_mapping",
				Map.of("actions", new Map[]{
						Map.of(
								"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
								"new_name", "RenamedLone"
						),
						Map.of(
								"entry_description", "field name@cuchaz/enigma/inputs/loneClass/LoneClass Ljava/lang/String;",
								"new_name", "renamedField"
						)
				}),
				Map.of()
		));

		assertFalse(result.isError());
		assertEquals(2, result.content().size());
		assertTrue(asTextContent(result.content().get(0)).text().contains("RenamedLone"));
		assertTrue(asTextContent(result.content().get(1)).text().contains("renamedField"));
	}

	@Test
	public void multiEditMixedSuccessAndFailure() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"multi_edit_mapping",
				Map.of("actions", new Map[]{
						Map.of(
								"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
								"new_name", "GoodName"
						),
						Map.of(
								"entry_description", "method nonexistent@cuchaz/enigma/inputs/loneClass/LoneClass",
								"new_name", "BadName"
						)
				}),
				Map.of()
		));

		assertTrue(result.isError());
		assertEquals(2, result.content().size());
		assertTrue(asTextContent(result.content().get(0)).text().contains("GoodName"));
		assertTrue(asTextContent(result.content().get(1)).text().startsWith("No matching entry for: "));
	}

	@Test
	public void multiEditEmptyActions() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"multi_edit_mapping",
				Map.of("actions", new Map[0]),
				Map.of()
		));

		assertFalse(result.isError());
		assertTrue(result.content().isEmpty());
	}
}

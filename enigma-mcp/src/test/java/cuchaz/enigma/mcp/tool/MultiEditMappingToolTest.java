package cuchaz.enigma.mcp.tool;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Assert;
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

		Assert.assertFalse(result.isError());
		Assert.assertEquals(2, result.content().size());
		Assert.assertTrue(asTextContent(result.content().get(0)).text().contains("RenamedLone"));
		Assert.assertTrue(asTextContent(result.content().get(1)).text().contains("renamedField"));
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

		Assert.assertTrue(result.isError());
		Assert.assertEquals(2, result.content().size());
		Assert.assertTrue(asTextContent(result.content().get(0)).text().contains("GoodName"));
		Assert.assertTrue(asTextContent(result.content().get(1)).text().startsWith("No matching entry for: "));
	}

	@Test
	public void multiEditEmptyActions() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"multi_edit_mapping",
				Map.of("actions", new Map[0]),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		Assert.assertTrue(result.content().isEmpty());
	}
}

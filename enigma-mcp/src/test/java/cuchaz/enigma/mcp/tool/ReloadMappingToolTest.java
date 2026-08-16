package cuchaz.enigma.mcp.tool;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Assert;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link ReloadMappingTool}.
 */
public class ReloadMappingToolTest extends Global {
	@Test
	public void clearMappingWithFlag() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"reload_mapping",
				Map.of("clear_if_all_null", true),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Mapping cleared"));
	}

	@Test
	public void reloadRequiresExistingMapping() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"reload_mapping",
				Map.of(),
				Map.of()
		));

		Assert.assertTrue(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("no existed mapping param"));
	}
}

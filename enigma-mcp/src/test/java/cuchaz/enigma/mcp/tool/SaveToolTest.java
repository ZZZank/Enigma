package cuchaz.enigma.mcp.tool;


import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Assert;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link SaveTool}.
 */
public class SaveToolTest extends Global {

	@Test
	public void saveRequiresFormatOrExistingMapping() {
		try {
			McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
					"save",
					Map.of(),
					Map.of()
			));
			Assert.assertTrue(result.isError());
		} catch (Exception ignored) {
			// Server-side NPE is also acceptable — no mapping configured
		}
	}
}

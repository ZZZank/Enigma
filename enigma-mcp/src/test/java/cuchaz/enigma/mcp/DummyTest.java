package cuchaz.enigma.mcp;

import cuchaz.enigma.Enigma;
import cuchaz.enigma.mcp.tool.GetEnigmaInfoTool;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author ZZZank
 */
public class DummyTest extends Global {
	/// @see GetEnigmaInfoTool
	@Test
	public void getEnigmaInfo() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"get_enigma_info",
				Map.of(),
				Map.of()
		));

		Assert.assertEquals(1, result.content().size());

		McpSchema.TextContent content = (McpSchema.TextContent) result.content().get(0);

		Assert.assertTrue(content.text().startsWith("Enigma Version: " + Enigma.VERSION));
	}
}

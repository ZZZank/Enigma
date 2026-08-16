package cuchaz.enigma.mcp.tool;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Assert;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/// Tests for [DecompileTool].
///
/// Note: the vineflower decompiler has a known NPE in the enigma test setup
/// (`Cannot invoke String.hashCode() because <local2> is null`),
/// so we only test with the bytecode decompiler which is unaffected.
public class DecompileToolTest extends Global {

	@Test
	public void decompile() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"decompile",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/loneClass/LoneClass"),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		Assert.assertTrue(text, removeIndent(text).startsWith(removeIndent("""
				package cuchaz.enigma.inputs.loneClass;
				
				public class LoneClass {
					private String name;
				
					public LoneClass(String name) {
						this.name = name;
					}
				
					public String getName() {
						return this.name;
					}
				}
				""")));
	}

	private String removeIndent(String text) {
		return text.replace("    ", "").replace("\t", "").replace("\n", "");
	}

	@Test
	public void decompileClassNotFound() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"decompile",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/nonexistent/Foo"
				),
				Map.of()
		));

		Assert.assertTrue(result.isError());
	}

	@Test
	public void decompileInvalidDecompiler() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"decompile",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/loneClass/LoneClass",
						"decompiler", "nonexistent_decompiler"
				),
				Map.of()
		));

		Assert.assertTrue(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Cannot find decompiler"));
	}

	@Test
	public void decompileA_BasicWithBytecode() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"decompile",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/translation/A_Basic",
						"decompiler", "bytecode"
				),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("public class cuchaz/enigma/inputs/translation/A_Basic"));
	}
}

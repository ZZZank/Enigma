package cuchaz.enigma.mcp.tool;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Assert;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link FindInheritanceTool}.
 *
 * <p>Enum values must be lowercase to match JSON Schema.
 */
public class FindInheritanceToolTest extends Global {

	@Test
	public void getParents() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/inheritanceTree/SubclassA",
						"query", "parents"
				),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Parents"));
		Assert.assertTrue(text, text.contains("inheritanceTree/BaseClass"));
	}

	@Test
	public void getChildren() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/inheritanceTree/BaseClass",
						"query", "children"
				),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Children"));
		Assert.assertTrue(text, text.contains("SubclassA"));
		Assert.assertTrue(text, text.contains("SubclassB"));
	}

	@Test
	public void getAncestors() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/inheritanceTree/SubsubclassAA",
						"query", "ancestors"
				),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Ancestors"));
		Assert.assertTrue(text, text.contains("SubclassA"));
		Assert.assertTrue(text, text.contains("BaseClass"));
	}

	@Test
	public void getDescendants() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/inheritanceTree/SubclassA",
						"query", "descendents"
				),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Descendants"));
		Assert.assertTrue(text, text.contains("SubsubclassAA"));
	}

	@Test
	public void getRelationParentChild() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/inheritanceTree/SubclassA",
						"query", "relation",
						"other_class", "cuchaz/enigma/inputs/inheritanceTree/BaseClass"
				),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Relation"));
	}

	@Test
	public void getNoChildren() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/inheritanceTree/SubsubclassAA",
						"query", "children"
				),
				Map.of()
		));

		Assert.assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("(none)"));
	}

	@Test
	public void classNotFound() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/nonexistent/Foo",
						"query", "parents"
				),
				Map.of()
		));

		Assert.assertTrue(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("Class not found"));
	}

	@Test
	public void relationWithoutOtherClass() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_inheritance",
				Map.of(
						"class_name", "cuchaz/enigma/inputs/inheritanceTree/BaseClass",
						"query", "relation"
				),
				Map.of()
		));

		Assert.assertTrue(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		Assert.assertTrue(text, text.contains("relation query requires"));
	}
}

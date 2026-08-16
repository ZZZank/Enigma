package cuchaz.enigma.mcp.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Before;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link EditMappingTool}.
 */
public class EditMappingToolTest extends Global {

	@Before
	public void setUp() {
		clearMapping();
	}

	@Test
	public void setClassName() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
						"new_name", "RenamedLone"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("Updated"));
		assertTrue(text.contains("name: RenamedLone"));
	}

	@Test
	public void setFieldName() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "field name@cuchaz/enigma/inputs/loneClass/LoneClass Ljava/lang/String;",
						"new_name", "displayName"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("Updated"));
		assertTrue(text.contains("name: displayName"));
	}

	@Test
	public void setJavadoc() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
						"javadoc", "A test class"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("javadoc: A test class"));
	}

	@Test
	public void setAccessModifier() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "field name@cuchaz/enigma/inputs/loneClass/LoneClass Ljava/lang/String;",
						"access", "public"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("access: PUBLIC"));
	}

	@Test
	public void removeMappingByAllNull() {
		CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
						"new_name", "TemporaryName"
				),
				Map.of()
		));

		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of("entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("Updated"));
	}

	@Test
	public void editInvalidMethod() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "method nonexistent@cuchaz/enigma/inputs/loneClass/LoneClass",
						"new_name", "foo"
				),
				Map.of()
		));

		assertTrue(result.isError());
	}

	@Test
	public void editValidNonexistentClassIsOk() {
		// Adding mapping
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/nonexistent/Foo",
						"new_name", "Foo"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("name: Foo"));
	}

	@Test
	public void setEverythingTogether() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"edit_mapping",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
						"new_name", "FullEdit",
						"javadoc", "Javadoc test",
						"access", "protected"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("name: FullEdit"));
		assertTrue(text.contains("javadoc: Javadoc test"));
		assertTrue(text.contains("access: PROTECTED"));
	}
}

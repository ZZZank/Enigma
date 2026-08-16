package cuchaz.enigma.mcp.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Before;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link ListMembersTool}.
 */
public class ListMembersToolTest extends Global {
	@Before
	public void setUp() {
		clearMapping();
	}

	@Test
	public void listAllMembers() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"list_members",
				Map.of("class_name", "cuchaz/enigma/inputs/loneClass/LoneClass", "member_type", "all"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		assertTrue(text.contains("Class: cuchaz/enigma/inputs/loneClass/LoneClass"));
		assertTrue(text.contains("field name"));
		assertTrue(text.contains("method getName"));
	}

	@Test
	public void listOnlyMethods() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"list_members",
				Map.of("class_name", "cuchaz/enigma/inputs/loneClass/LoneClass", "member_type", "method"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		assertTrue(text.contains("method getName"));
		assertFalse(text.contains("field name"));
	}

	@Test
	public void listOnlyFields() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"list_members",
				Map.of("class_name", "cuchaz/enigma/inputs/translation/A_Basic", "member_type", "field"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		assertTrue(text.contains("field one"));
		assertTrue(text.contains("field two"));
		assertTrue(text.contains("field three"));
		assertFalse(text.contains("method m"));
	}

	@Test
	public void listClassNotFound() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"list_members",
				Map.of("class_name", "cuchaz/enigma/inputs/nonexistent/Foo", "member_type", "all"),
				Map.of()
		));

		assertTrue(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		assertTrue(text.contains("Class not found"));
	}

	@Test
	public void listInnerClasses() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"list_members",
				Map.of("class_name", "cuchaz/enigma/inputs/innerClasses/D_Simple", "member_type", "all"),
				Map.of()
		));

		assertFalse(result.isError());
		String text = asTextContent(result.content().get(0)).text();
		assertTrue(text.contains("D_Simple"));
		assertTrue(text.contains("class cuchaz/enigma/inputs/innerClasses/D_Simple$Inner"));
	}
}

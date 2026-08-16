package cuchaz.enigma.mcp.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.Test;

import cuchaz.enigma.mcp.Global;

/**
 * Tests for {@link FindReferenceTool}.
 *
 * <p>Enum values must be lowercase to match JSON Schema.
 */
public class FindReferenceToolTest extends Global {

	@Test
	public void referencesByUsageForClass() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_reference",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
						"reference_type", "references_by_usage"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		System.out.println(text);
		assertTrue(text.contains("References"));
		assertTrue(text.contains("loneClass/LoneClass"));
	}

	@Test
	public void referencesByUsageForMethod() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_reference",
				Map.of(
						"entry_description", "method getName@cuchaz/enigma/inputs/loneClass/LoneClass ()Ljava/lang/String;",
						"reference_type", "references_by_usage"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("References"));
	}

	@Test
	public void referencesInDeclaration() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_reference",
				Map.of(
						"entry_description", "class java/lang/String",
						"reference_type", "references_in_declaration"
				),
				Map.of()
		));

		assertFalse(result.isError());
		String text = ((McpSchema.TextContent) result.content().get(0)).text();
		assertTrue(text.contains("References"));
	}

	@Test
	public void referencesInvalidEntry() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_reference",
				Map.of(
						"entry_description", "method nonexistent@cuchaz/enigma/inputs/loneClass/LoneClass",
						"reference_type", "references_by_usage"
				),
				Map.of()
		));

		assertTrue(result.isError());
	}

	@Test
	public void referencesInvalidTypeForDeclaration() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_reference",
				Map.of(
						"entry_description", "method getName@cuchaz/enigma/inputs/loneClass/LoneClass ()Ljava/lang/String;",
						"reference_type", "references_in_declaration"
				),
				Map.of()
		));

		assertTrue(result.isError());
	}

	@Test
	public void referencesInvalidTypeForMethodsReferencedBy() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_reference",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
						"reference_type", "methods_referenced_by"
				),
				Map.of()
		));

		assertTrue(result.isError());
	}

	@Test
	public void referencesUnknownQueryType() {
		McpSchema.CallToolResult result = CLIENT.callTool(new McpSchema.CallToolRequest(
				"find_reference",
				Map.of(
						"entry_description", "class cuchaz/enigma/inputs/loneClass/LoneClass",
						"reference_type", "unknown_type"
				),
				Map.of()
		));

		assertTrue(result.isError());
	}
}

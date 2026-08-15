package cuchaz.enigma.mcp.tool;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.generator.impl.module.EnumModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.JsonNodeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author ZZZank
 */
public interface TypedArgTool<T> {
	SchemaGeneratorConfig COMMON_CONFIG = createCommonConfig();

	/// @param lock [ReadWriteLock#readLock()] is used when the tool is {@link TypedArgTool#configureToolBuilder(McpSchema.Tool.Builder) annotated as read-only}, [ReadWriteLock#writeLock()] otherwise
	static <T> McpServerFeatures.SyncToolSpecification createMcpTool(
			SchemaGeneratorConfig config,
			TypedArgTool<T> tool,
			ReadWriteLock lock) {
		JsonNode jsonSchema = new SchemaGenerator(config).generateSchema(tool.argObjectType());

		ObjectMapper objectMapper = config.getObjectMapper();

		@SuppressWarnings("unchecked")
		Map<String, Object> schema = objectMapper.convertValue(jsonSchema, Map.class);
		String description = (String) schema.remove("description");

		McpSchema.Tool.Builder builder = McpSchema.Tool.builder(tool.name(), schema);

		if (description != null) {
			builder.description(description);
		}

		McpSchema.Tool builtTool = tool.configureToolBuilder(builder).build();
		boolean useReadLock = !tool.requiresWriteLock()
				&& builtTool.annotations() != null
				&& builtTool.annotations().readOnlyHint() == Boolean.TRUE;

		return new McpServerFeatures.SyncToolSpecification(builtTool, (exchange, request) -> {
			Lock toolLock = useReadLock ? lock.readLock() : lock.writeLock();

			toolLock.lock();
			try {
				T argObject = objectMapper.convertValue(request.arguments(), tool.argObjectType());
				return tool.callTool(exchange, request, argObject);
			} finally {
				toolLock.unlock();
			}
		});
	}

	/// Name of this tool.
	String name();

	/// Class that describes the structure of tool call arg. Info annotations like [JsonProperty], [com.fasterxml.jackson.annotation.JsonPropertyDescription]
	/// will be read for automatic JSON Schema generation.
	///
	/// NOTE: [com.fasterxml.jackson.annotation.JsonClassDescription] applied to argObjectType class itself will be used
	/// as description for the tool, so descriptions of `@JsonClassDescription(...)` should describe the tool, instead
	/// of arg object.
	Class<T> argObjectType();

	/// Modify tool builder. Tool description can be generated from [#argObjectType()] so no need to do it here.
	default McpSchema.Tool.Builder configureToolBuilder(McpSchema.Tool.Builder builder) {
		return builder;
	}

	/// @see #createMcpTool(SchemaGeneratorConfig, TypedArgTool, ReadWriteLock)
	default boolean requiresWriteLock() {
		return false;
	}

	McpSchema.CallToolResult callTool(McpSyncServerExchange exchange, McpSchema.CallToolRequest request, T arg);

	private static SchemaGeneratorConfig createCommonConfig() {
		JsonMapper jsonMapper = JsonMapper.builder()
				/// @see SchemaGeneratorConfigBuilder#createDefaultObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT)
				.enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS)
				.enable(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)
				// additional: lowercase enum name
				.enumNamingStrategy(EnumNamingStrategies.SNAKE_CASE)
				.build();

		SchemaGeneratorConfigBuilder builder = new SchemaGeneratorConfigBuilder(jsonMapper, SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
				.with(new JacksonSchemaModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED))
				.with(new EnumModule(e -> e.name().toLowerCase(Locale.ROOT)));

		builder.forFields().withDefaultResolver(field -> {
			JsonProperty annotation = field.getAnnotationConsideringFieldAndGetter(JsonProperty.class);

			if (annotation == null || annotation.defaultValue().isEmpty()) {
				return null;
			}

			return builder.getObjectMapper().convertValue(annotation.defaultValue(), field.getType().getErasedType());
		});

		return builder.build();
	}
}
